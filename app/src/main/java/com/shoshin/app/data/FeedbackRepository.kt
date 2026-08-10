package com.Shoshin.app.data

import com.Shoshin.app.data.db.dao.FeedbackDao
import com.Shoshin.app.data.db.entities.FeedbackEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FeedbackRepository(
    private val feedbackDao: FeedbackDao,
    private val firestore: FirebaseFirestore
) {
    suspend fun submitFeedback(userId: String, text: String, appVersion: String) {
        val feedback = FeedbackEntity(
            feedbackId = UUID.randomUUID().toString(),
            userId = userId,
            text = text,
            appVersion = appVersion,
            createdAt = System.currentTimeMillis(),
            status = "pending",
            syncStatus = "pending"
        )
        feedbackDao.insertFeedback(feedback)
        try {
            firestore.collection("feedback").document(feedback.feedbackId).set(feedback).await()
        } catch (e: Exception) {
            android.util.Log.e("FeedbackRepository", "Cloud sync failed: ${e.message}")
        }
    }
}
