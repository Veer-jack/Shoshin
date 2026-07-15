package com.example.shoshinapp.alarm

import android.app.*
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.shoshinapp.MainActivity
import com.example.shoshinapp.R
import com.example.shoshinapp.data.ShoshinRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AlarmService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private val CHANNEL_ID = "shoshin_alarm_channel"
    private val NOTIF_ID   = 1001

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val routineName = intent?.getStringExtra("routine_name") ?: "Morning Walk"
        val repo = ShoshinRepository(applicationContext)

        // Start foreground with notification
        val launchIntent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra("open_screen", "activation")
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Time to begin.")
            .setContentText("$routineName starts now. Solve to silence.")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(pendingIntent, true)
            .setContentIntent(pendingIntent)
            .setAutoCancel(false)
            .setOngoing(true)
            .build()

        startForeground(NOTIF_ID, notification)

        // Play alarm sound with user settings
        CoroutineScope(Dispatchers.IO).launch {
            val tone = repo.alarmTone.first()
            val intensity = repo.alarmIntensity.first()
            val volume = intensity / 10f

            val alarmUri = when (tone) {
                "Forest" -> RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
                "Rising Sun" -> RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                else -> RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            }

            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                setDataSource(applicationContext, alarmUri)
                setVolume(volume, volume)
                isLooping = true
                prepare()
                start()
            }
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        super.onDestroy()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Shoshin Alarm",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Shoshin morning activation alarm"
                enableVibration(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
    }
}
