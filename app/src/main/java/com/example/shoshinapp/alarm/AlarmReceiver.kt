package com.example.shoshinapp.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                // Re-schedule alarm after reboot if one was set
                // Read from DataStore and reschedule
            }
            "com.example.shoshinapp.ALARM_TRIGGER" -> {
                val serviceIntent = Intent(context, AlarmService::class.java).apply {
                    putExtra("routine_name", intent.getStringExtra("routine_name") ?: "Morning Walk")
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent)
                } else {
                    context.startService(serviceIntent)
                }
            }
        }
    }
}
