package com.Shoshin.app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import androidx.compose.runtime.*
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.Shoshin.app.data.AuthRepository
import com.Shoshin.app.data.ShoshinRepository
import com.Shoshin.app.data.db.AppDatabase
import com.Shoshin.app.navigation.ShoshinNavGraph
import com.Shoshin.app.sync.*
import com.Shoshin.app.ui.theme.ShoshinTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.Shoshin.app.utils.AnalyticsManager
import com.Shoshin.app.utils.LocationHelper
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.os.Build
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    
    private lateinit var authRepository: AuthRepository
    private lateinit var syncManager: SyncManager
    private lateinit var networkMonitor: NetworkStateMonitor
    private lateinit var conflictResolver: ConflictResolver
    private lateinit var shoshinRepository: ShoshinRepository
    
    private var navController: NavHostController? = null
    private val currentIntent = mutableStateOf<Intent?>(null)

    /**
     * Login/onboarding state lives in DataStore, which reads asynchronously. Until that
     * first read lands we cannot pick a start destination, so the system splash is held
     * on screen — otherwise the app briefly shows the Welcome screen to a signed-in user
     * and only then corrects itself.
     */
    private var authStateResolved = false

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().setKeepOnScreenCondition { !authStateResolved }
        super.onCreate(savedInstanceState)
        
        currentIntent.value = intent
        
        // ... (Lock screen logic)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                android.view.WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }
        
        enableEdgeToEdge()

        // ... (Initializations)
        val firebaseAuth = FirebaseAuth.getInstance()
        // firebaseAuth.firebaseAuthSettings.forceRecaptchaFlowForTesting(true)
        val firestore = FirebaseFirestore.getInstance()
        val database = AppDatabase.getInstance(applicationContext)

        authRepository = AuthRepository(firebaseAuth)
        conflictResolver = ConflictResolver()
        syncManager = SyncManager(database, firestore, authRepository, conflictResolver)
        networkMonitor = NetworkStateMonitor(applicationContext)
        shoshinRepository = ShoshinRepository(applicationContext)

        SyncWorker.scheduleSyncWork(applicationContext)
        WeeklySummaryWorker.schedule(applicationContext)

        // Notification Permission
        requestNotificationPermission()

        setContent {
            ShoshinTheme {
                val controller = rememberNavController()
                navController = controller
                
                val intentState by currentIntent

                // null = DataStore hasn't answered yet. Build the NavHost only once it has,
                // so startDestination is right on the first composition.
                val authState by remember {
                    kotlinx.coroutines.flow.combine(
                        shoshinRepository.isLoggedIn,
                        shoshinRepository.onboardingDone
                    ) { loggedIn, onboardingDone -> loggedIn to onboardingDone }
                }.collectAsState(initial = null)

                LaunchedEffect(authState) {
                    if (authState != null) authStateResolved = true
                }

                // Deep links must wait for the graph to exist, or navigate() throws.
                LaunchedEffect(intentState, authState) {
                    if (authState != null) intentState?.let { handleIntent(it) }
                }

                authState?.let { (isLoggedIn, onboardingDone) ->
                    ShoshinNavGraph(
                        navController = controller,
                        database = database,
                        shoshinRepository = shoshinRepository,
                        syncManager = syncManager,
                        networkMonitor = networkMonitor,
                        conflictResolver = conflictResolver,
                        isLoggedIn = isLoggedIn,
                        hasCompletedOnboarding = onboardingDone,
                        deepLinkCode = when {
                            intent.data?.scheme == "shoshin" && intent.data?.host == "invite" -> intent.data?.getQueryParameter("code")
                            intent.data?.path?.contains("join") == true -> intent.data?.lastPathSegment
                            else -> null
                        }
                    )
                }
            }
        }

        // Track App Open
        lifecycleScope.launch {
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser != null) {
                database.userDao().getUser(firebaseUser.uid)?.let { user ->
                    val now = System.currentTimeMillis()
                    database.userDao().updateUser(user.copy(
                        lastOpenDate = now,
                        totalSessionCount = user.totalSessionCount + 1
                    ))
                    
                    AnalyticsManager.logAppOpened(
                        daysSinceSignup = TimeUnit.MILLISECONDS.toDays(now - user.createdAt).toInt(),
                        daysSinceLastOpen = if (user.lastOpenDate > 0) TimeUnit.MILLISECONDS.toDays(now - user.lastOpenDate).toInt() else 0,
                        streak = user.currentStreak,
                        userType = "professional"
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        currentIntent.value = intent
    }

    private fun handleIntent(intent: Intent) {
        val openCheckpoint = intent.getBooleanExtra(ShoshinNotificationManager.EXTRA_OPEN_CHECKPOINT, false)
        val navigateTo = intent.getStringExtra("navigate_to")
        val data = intent.data

        Log.d("MainActivity", "handleIntent: openCheckpoint=$openCheckpoint, navigateTo=$navigateTo, data=$data")

        if (openCheckpoint) {
            navController?.navigate(com.Shoshin.app.navigation.ShRoutes.ACTIVATION)
            intent.removeExtra(ShoshinNotificationManager.EXTRA_OPEN_CHECKPOINT)
        } else if (navigateTo != null) {
            navController?.navigate(navigateTo)
            intent.removeExtra("navigate_to")
        } else if (data?.scheme == "shoshin" && data.host == "group") {
            val inviteCode = data.lastPathSegment
            if (!inviteCode.isNullOrBlank()) {
                navController?.navigate(com.Shoshin.app.navigation.ShRoutes.groupPreview(inviteCode))
            }
        } else if (data?.scheme == "shoshin" && data.host == "invite") {
            // Referral code pre-fill only happens via the cold-start path (onCreate's deepLinkCode ->
            // ShoshinNavGraph -> AuthScreen.initialReferralCode). AUTH isn't a param-carrying route, so
            // if the app is already running when this link is tapped, we can open Auth but not prefill it.
            navController?.navigate(com.Shoshin.app.navigation.ShRoutes.AUTH)
        }
    }

    override fun onDestroy() {
        SyncWorker.cancelSyncWork(applicationContext)
        // The connectivity callback outlives this Activity, and a new monitor is built in
        // every onCreate — without this, each recreation leaves another one registered.
        networkMonitor.unregister()
        super.onDestroy()
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != 
                PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }
    }
}
