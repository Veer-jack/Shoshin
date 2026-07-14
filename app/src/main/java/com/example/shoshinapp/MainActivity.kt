package com.example.shoshinapp

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
import com.example.shoshinapp.data.AuthRepository
import com.example.shoshinapp.data.ShoshinRepository
import com.example.shoshinapp.data.db.AppDatabase
import com.example.shoshinapp.navigation.ShoshinNavGraph
import com.example.shoshinapp.sync.*
import com.example.shoshinapp.ui.theme.ShoshinTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.shoshinapp.utils.AnalyticsManager
import com.example.shoshinapp.utils.LocationHelper
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

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
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
        firebaseAuth.firebaseAuthSettings.forceRecaptchaFlowForTesting(true)
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
                LaunchedEffect(intentState) {
                    intentState?.let { handleIntent(it) }
                }

                val isLoggedIn by shoshinRepository.isLoggedIn.collectAsState(initial = false)
                val onboardingDone by shoshinRepository.onboardingDone.collectAsState(initial = false)

                ShoshinNavGraph(
                    navController = controller,
                    database = database,
                    shoshinRepository = shoshinRepository,
                    syncManager = syncManager,
                    networkMonitor = networkMonitor,
                    conflictResolver = conflictResolver,
                    isLoggedIn = isLoggedIn,
                    hasCompletedOnboarding = onboardingDone,
                    deepLinkCode = intent.data?.lastPathSegment.takeIf { intent.data?.path?.contains("join") == true }
                )
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
        
        Log.d("MainActivity", "handleIntent: openCheckpoint=$openCheckpoint, navigateTo=$navigateTo")
        
        if (openCheckpoint) {
            navController?.navigate(com.example.shoshinapp.navigation.ShRoutes.ACTIVATION)
            intent.removeExtra(ShoshinNotificationManager.EXTRA_OPEN_CHECKPOINT)
        } else if (navigateTo != null) {
            navController?.navigate(navigateTo)
            intent.removeExtra("navigate_to")
        }
    }

    override fun onDestroy() {
        SyncWorker.cancelSyncWork(applicationContext)
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
