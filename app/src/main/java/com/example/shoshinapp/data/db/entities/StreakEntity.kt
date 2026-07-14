package com.example.shoshinapp.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "streaks")
data class StreakEntity(
    @PrimaryKey val streakId: String = "",
    val userId: String = "",
    val date: String = "",
    val completed: Boolean = false,
    val timestamp: Long = 0,
    val syncStatus: String = "pending", // pending, synced, failed
    val lastUpdated: Long = System.currentTimeMillis()
)
