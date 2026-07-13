package com.example.shoshinapp

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import java.util.*

class ShoshinNotificationManager(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        const val CHANNEL_ID = "shoshin_reminders"
        const val CHANNEL_NAME = "Daily Reminders"
        const val NOTIFICATION_ID = 1001
        const val ACTION_NOTIFICATION_ALARM = "com.example.shoshinapp.NOTIFICATION_ALARM"
        const val EXTRA_OPEN_CHECKPOINT = "open_checkpoint"
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Reminders to complete your morning checkpoint"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun scheduleNotification(hour: Int, minute: Int) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            
            // If the time is in the past today, schedule for tomorrow
            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1)
            }
        }

        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = ACTION_NOTIFICATION_ALARM
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
            Log.d("NotificationManager", "Notification scheduled for ${calendar.time}")
        } catch (e: SecurityException) {
            Log.e("NotificationManager", "Failed to schedule exact alarm", e)
            // Fallback to inexact if permission is missing
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }

    fun cancelNotification() {
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = ACTION_NOTIFICATION_ALARM
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        Log.d("NotificationManager", "Notification cancelled")
    }

    fun showNotification(title: String, message: String, streak: Int) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(EXTRA_OPEN_CHECKPOINT, true)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val skipIntent = Intent(context, NotificationReceiver::class.java).apply {
            action = "com.example.shoshinapp.SKIP_TODAY"
        }
        val skipPendingIntent = PendingIntent.getBroadcast(
            context,
            1,
            skipIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_bolt_heavy) // Assuming this exists based on FAB
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .addAction(0, "Open Checkpoint", pendingIntent)
            .addAction(0, "Skip Today", skipPendingIntent)

        notificationManager.notify(NOTIFICATION_ID, builder.build())
        Log.d("NotificationManager", "Notification shown: $title")
    }

    fun showMilestoneNotification(days: Int) {
        val title = when (days) {
            7 -> "🎉 7-Day Champion!"
            30 -> "🏆 30-Day Master!"
            100 -> "👑 Legend Status!"
            365 -> "🌟 One Year Unstoppable!"
            else -> "$days Day Milestone!"
        }
        
        val message = "You've kept the morning for $days days. Share your progress!"
        
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            2,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_bolt_heavy)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        notificationManager.notify(NOTIFICATION_ID + days, builder.build())
    }

    fun showGenericNotification(title: String, message: String, route: String? = null) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            if (route != null) putExtra("navigate_to", route)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_bolt_heavy)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        notificationManager.notify(NOTIFICATION_ID + 500, builder.build())
    }
}
