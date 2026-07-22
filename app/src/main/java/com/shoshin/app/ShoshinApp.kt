package com.shoshin.app

import android.app.Application
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.analytics.FirebaseAnalytics
import com.shoshin.app.utils.AnalyticsManager

class ShoshinApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize App Check
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            DebugAppCheckProviderFactory.getInstance()
        )

        // Initialize Analytics
        AnalyticsManager.initialize(this)
        FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(true)
    }
}
