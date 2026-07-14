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
    val notificationTime: String = "06:00",
    val notificationSound: Boolean = true,
    val notificationVibration: Boolean = true,
    val productiveStartTime: String = "06:00",
    val productiveEndTime: String = "22:00",
    val onboardingCompleted: Boolean = false,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val streakStartDate: Long = 0,
    val lastCheckpointDate: Long = 0,
    val totalActivations: Int = 0,
    val streakFreezes: Int = 0,
    val freezesUsedThisMonth: Int = 0,
    val lastFreezeResetDate: Long = 0,
    val inviteCode: String = "",
    val referredByUserId: String? = null,
    val friendCount: Int = 0,
    val pendingRequestCount: Int = 0,
    val isPro: Boolean = false,
    val proExpiryDate: Long = 0,
    val lastSyncTime: Long = 0,
    val lastOpenDate: Long = 0,
    val totalSessionCount: Int = 0,
    val totalSessionMinutes: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val lastUpdated: Long = System.currentTimeMillis()
)
