package com.example.shoshinapp.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.shoshinapp.data.AuthRepository
import com.example.shoshinapp.data.db.AppDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.TimeUnit

class SyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val authRepository = AuthRepository(FirebaseAuth.getInstance())
            val db = AppDatabase.getInstance(applicationContext)
            val firestore = FirebaseFirestore.getInstance()
            val conflictResolver = ConflictResolver()

            val syncManager = SyncManager(db, firestore, authRepository, conflictResolver)
            syncManager.syncAll()

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    companion object {
        private const val SYNC_WORK_NAME = "shoshin_sync_work"

        fun scheduleSyncWork(context: Context) {
            val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(
                15, TimeUnit.MINUTES
            ).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                SYNC_WORK_NAME,
                androidx.work.ExistingPeriodicWorkPolicy.KEEP,
                syncRequest
            )
        }

        fun cancelSyncWork(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(SYNC_WORK_NAME)
        }
    }
}