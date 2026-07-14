package com.example.shoshinapp.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val notificationId: String = "",
    val userId: String = "",
    val type: String = "", // welcome, streak, reminder, achievement, social
    val title: String = "",
    val body: String = "",
    val iconRes: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val syncStatus: String = "pending"
)
