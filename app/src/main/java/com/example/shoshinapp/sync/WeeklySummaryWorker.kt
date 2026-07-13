package com.example.shoshinapp.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.shoshinapp.ShoshinNotificationManager
import com.example.shoshinapp.data.db.AppDatabase
import com.example.shoshinapp.navigation.ShRoutes
import com.google.firebase.auth.FirebaseAuth
import java.util.concurrent.TimeUnit

class WeeklySummaryWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return Result.success()
            val db = AppDatabase.getInstance(applicationContext)
            
            // Last 7 days
            val weekStart = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000)
            val count = db.statsDao().getCheckpointCountSince(userId, weekStart)
            
            val notificationManager = ShoshinNotificationManager(applicationContext)
            notificationManager.showGenericNotification(
                title = "Your week in review 📊",
                message = "You completed $count checkpoints this week. See your progress!",
                route = ShRoutes.STATS
            )

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    companion object {
        private const val WORK_NAME = "weekly_summary_work"

        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<WeeklySummaryWorker>(
                7, TimeUnit.DAYS
            ).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                androidx.work.ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }
}
