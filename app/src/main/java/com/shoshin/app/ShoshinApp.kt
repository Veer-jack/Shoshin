package com.Shoshin.app

import android.app.Application
import android.util.Log
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.analytics.FirebaseAnalytics
import com.Shoshin.app.utils.AnalyticsManager
import com.Shoshin.app.BuildConfig

class ShoshinApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize App Check
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        if (BuildConfig.DEBUG) {
            Log.d("ShoshinApp", "Installing DebugAppCheckProviderFactory")
            // This will trigger the debug token to be printed in Logcat
            firebaseAppCheck.installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance()
            )
        } else {
            Log.d("ShoshinApp", "Installing PlayIntegrityAppCheckProviderFactory")
            firebaseAppCheck.installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance()
            )
        }

        firebaseAppCheck.addAppCheckListener { tokenResult ->
            Log.d("ShoshinApp", "App Check Token update: ${if (tokenResult.token != null) "Success" else "Failure"}")
        }

        Log.d("ShoshinApp", "Firebase & App Check Initialization Complete. Application Ready.")

        // Initialize Analytics
        AnalyticsManager.initialize(this)
        FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(true)
    }
}
