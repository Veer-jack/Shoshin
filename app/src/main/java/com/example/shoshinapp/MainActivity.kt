package com.example.shoshinapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import androidx.compose.runtime.*
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
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

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        
        // Handle lock screen visibility based on API version
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
        
        // Enable edge-to-edge as per Shoshin design principles
        enableEdgeToEdge()

        // Initialize Firebase and local database
        val firebaseAuth = FirebaseAuth.getInstance()
        
        // Enable reCAPTCHA flow for testing to fix "missing valid app identifier" error in debug/test environments
        firebaseAuth.firebaseAuthSettings.forceRecaptchaFlowForTesting(true)

        val firestore = FirebaseFirestore.getInstance()
        val database = AppDatabase.getInstance(applicationContext)

        authRepository = AuthRepository(firebaseAuth)
        conflictResolver = ConflictResolver()
        syncManager = SyncManager(database, firestore, authRepository, conflictResolver)
        networkMonitor = NetworkStateMonitor(applicationContext)
        shoshinRepository = ShoshinRepository(applicationContext)

        // Schedule background sync
        SyncWorker.scheduleSyncWork(applicationContext)
        WeeklySummaryWorker.schedule(applicationContext)

        // Track App Open and Update Stats
        lifecycleScope.launch {
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser != null) {
                val user = database.userDao().getUser(firebaseUser.uid)
                if (user != null) {
                    val now = System.currentTimeMillis()
                    val daysSinceSignup = TimeUnit.MILLISECONDS.toDays(now - user.createdAt).toInt()
                    val daysSinceLastOpen = if (user.lastOpenDate > 0) {
                        TimeUnit.MILLISECONDS.toDays(now - user.lastOpenDate).toInt()
                    } else 0
                    
                    AnalyticsManager.logAppOpened(
                        daysSinceSignup = daysSinceSignup,
                        daysSinceLastOpen = daysSinceLastOpen,
                        streak = user.currentStreak,
                        userType = "professional" // Default for now
                    )

                    // Capture location on app open
                    val location = LocationHelper.getLastLocation(applicationContext)
                    location?.let {
                        AnalyticsManager.logLocationCaptured(it.latitude, it.longitude, "app_opened")
                    }
                    
                    database.userDao().updateUser(user.copy(
                        lastOpenDate = now,
                        totalSessionCount = user.totalSessionCount + 1
                    ))
                }
            }
        }

        // Notification Permission and Initialization
        requestNotificationPermission()
        val notificationManager = ShoshinNotificationManager(applicationContext)
        
        // Handle notification click intent
        val openCheckpoint = intent.getBooleanExtra(ShoshinNotificationManager.EXTRA_OPEN_CHECKPOINT, false)
        val navigateTo = intent.getStringExtra("navigate_to")
        
        // Handle Deep Link
        val deepLinkCode = intent.data?.lastPathSegment.takeIf { intent.data?.path?.contains("join") == true }

        setContent {
            ShoshinTheme {
                val navController = rememberNavController()
                
                // If opened from notification, we could navigate to activation
                LaunchedEffect(openCheckpoint, navigateTo) {
                    if (openCheckpoint) {
                        navController.navigate(com.example.shoshinapp.navigation.ShRoutes.ACTIVATION)
                    } else if (navigateTo != null) {
                        navController.navigate(navigateTo)
                    }
                }

                // If opened from deep link, we can pass it to AuthScreen or similar
                // For now, let's just make it available to the NavGraph

                val isLoggedIn by shoshinRepository.isLoggedIn.collectAsState(initial = false)
                val onboardingDone by shoshinRepository.onboardingDone.collectAsState(initial = false)

                ShoshinNavGraph(
                    navController = navController,
                    database = database,
                    shoshinRepository = shoshinRepository,
                    syncManager = syncManager,
                    networkMonitor = networkMonitor,
                    conflictResolver = conflictResolver,
                    isLoggedIn = isLoggedIn,
                    hasCompletedOnboarding = onboardingDone,
                    deepLinkCode = deepLinkCode
                )
            }
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
