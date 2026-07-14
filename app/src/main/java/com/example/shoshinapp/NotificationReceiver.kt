package com.example.shoshinapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.shoshinapp.data.ShoshinRepository
import com.example.shoshinapp.data.db.AppDatabase
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        Log.d("NotificationReceiver", "Received action: $action")

        val notificationManager = ShoshinNotificationManager(context)
        val repository = ShoshinRepository(context)

        when (action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                // Reschedule notification on reboot
                rescheduleNotification(context, notificationManager, repository)
            }
            ShoshinNotificationManager.ACTION_NOTIFICATION_ALARM -> {
                // Show the daily reminder
                showDailyNotification(context, notificationManager, repository)
                
                // Schedule for tomorrow
                rescheduleNotification(context, notificationManager, repository)
            }
            "com.example.shoshinapp.SKIP_TODAY" -> {
                Log.d("NotificationReceiver", "User skipped notification today")
                // Track skip today analytics would go here
                val androidNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
                androidNotificationManager.cancel(ShoshinNotificationManager.NOTIFICATION_ID)
            }
        }
    }

    private fun showDailyNotification(
        context: Context,
        notificationManager: ShoshinNotificationManager,
        repository: ShoshinRepository
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val isLoggedIn = repository.isLoggedIn.first()
            if (isLoggedIn) {
                val streak = repository.streakCount.first()
                val title = "Time for your checkpoint! Day $streak"
                val message = "Keep your morning routine going. Your bridge is waiting."
                
                notificationManager.showNotification(title, message, streak)

                // Also save to history
                try {
                    val db = AppDatabase.getInstance(context)
                    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                    if (uid.isNotEmpty()) {
                        db.notificationDao().insertNotification(
                            com.example.shoshinapp.data.db.entities.NotificationEntity(
                                notificationId = java.util.UUID.randomUUID().toString(),
                                userId = uid,
                                type = "reminder",
                                title = title,
                                body = message,
                                iconRes = R.drawable.ic_bolt_heavy
                            )
                        )
                    }
                } catch (e: Exception) {
                    Log.e("NotificationReceiver", "Failed to save to history", e)
                }
            }
        }
    }

    private fun rescheduleNotification(
        context: Context,
        notificationManager: ShoshinNotificationManager,
        repository: ShoshinRepository
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val hour = repository.alarmHour.first()
            val minute = repository.alarmMinute.first()
            // In a real app we'd check if notifications are enabled in DataStore/UserEntity
            notificationManager.scheduleNotification(hour, minute)
        }
    }
}
