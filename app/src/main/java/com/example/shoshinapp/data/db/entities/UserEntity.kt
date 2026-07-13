package com.example.shoshinapp.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val userId: String,
    val email: String?,
    val phone: String?,
    val displayName: String,
    val photoUrl: String?,
    val bio: String? = null,
    val profilePictureUrl: String? = null,
    val notificationsEnabled: Boolean = true,
    val productiveStartTime: String = "06:00",
    val productiveEndTime: String = "22:00",
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val totalActivations: Int = 0,
    val lastSyncTime: Long = 0,
    val lastUpdated: Long = System.currentTimeMillis()
)
