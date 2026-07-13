package com.example.shoshinapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    
    private lateinit var authRepository: AuthRepository
    private lateinit var syncManager: SyncManager
    private lateinit var networkMonitor: NetworkStateMonitor
    private lateinit var conflictResolver: ConflictResolver
    private lateinit var shoshinRepository: ShoshinRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        
        // Enable edge-to-edge as per Shoshin design principles
        enableEdgeToEdge()

        // Initialize Firebase App Check for local development authorization
        FirebaseApp.initializeApp(this)
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            DebugAppCheckProviderFactory.getInstance()
        )

        // Initialize Firebase and local database
        val firebaseAuth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()
        val database = AppDatabase.getInstance(applicationContext)

        authRepository = AuthRepository(firebaseAuth)
        conflictResolver = ConflictResolver()
        syncManager = SyncManager(database, firestore, authRepository, conflictResolver)
        networkMonitor = NetworkStateMonitor(applicationContext)
        shoshinRepository = ShoshinRepository(applicationContext)

        // Schedule background sync
        SyncWorker.scheduleSyncWork(applicationContext)

        setContent {
            ShoshinTheme {
                val navController = rememberNavController()
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
                    hasCompletedOnboarding = onboardingDone
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        SyncWorker.cancelSyncWork(applicationContext)
    }
}
