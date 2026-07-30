package com.Shoshin.app

import android.app.Application
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
            firebaseAppCheck.installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance()
            )
        } else {
            firebaseAppCheck.installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance()
            )
        }

        // Initialize Analytics
        AnalyticsManager.initialize(this)
        FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(true)
    }
}
