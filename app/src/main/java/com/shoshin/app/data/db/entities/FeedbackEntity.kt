package com.Shoshin.app.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "feedback")
data class FeedbackEntity(
    @PrimaryKey val feedbackId: String = "",
    val userId: String = "",
    val text: String = "",
    val appVersion: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val status: String = "pending", // pending, read, resolved
    val syncStatus: String = "pending"
)
