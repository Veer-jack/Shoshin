package com.example.shoshinapp.data.db.entities

import androidx.room.Entity

@Entity(tableName = "user_badges", primaryKeys = ["userId", "badgeId"])
data class BadgeEntity(
    val userId: String,
    val badgeId: String,
    val unlockedDate: Long = 0,
    val isLocked: Boolean = true,
    val progress: Int = 0,
    val lastUpdated: Long = System.currentTimeMillis()
)
