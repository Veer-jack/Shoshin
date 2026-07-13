package com.example.shoshinapp

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class ShoshinMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "New token: $token")
        // Upload token to backend if needed
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("FCM", "From: ${message.from}")

        // Handle data messages or notification messages from FCM
        message.notification?.let {
            val notificationManager = ShoshinNotificationManager(applicationContext)
            notificationManager.showNotification(
                it.title ?: "Shoshin Reminder",
                it.body ?: "Time for your morning routine",
                0
            )
        }
    }
}
