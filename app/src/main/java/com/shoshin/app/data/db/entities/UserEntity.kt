package com.Shoshin.app.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val userId: String = "",
    val email: String? = null,
    val phone: String? = null,
    val displayName: String = "",
    val photoUrl: String? = null,
    val bio: String? = null,
    val profilePictureUrl: String? = null,
    val height: Float? = null,
    val heightUnit: String = "cm",
    val weight: Float? = null,
    val weightUnit: String = "kg",
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
    val inviteCode: String = "",
    val referredByUserId: String? = null,
    val friendCount: Int = 0,
    val pendingRequestCount: Int = 0,
    val isPro: Boolean = false,
    val proExpiryDate: Long = 0,
    val lastSyncTime: Long = 0,
    val lastOpenDate: Long = 0,
    val lastRoutineStepIndex: Int = 0,
    val lastRoutineDate: String = "", // e.g. "2024-05-20"
    val totalSessionCount: Int = 0,
    val totalSessionMinutes: Int = 0,
    val unlockedBadgeIds: String = "", // comma-separated badge ids
    val createdAt: Long = System.currentTimeMillis(),
    val lastUpdated: Long = System.currentTimeMillis()
)

fun String.toBadgeIdList(): List<String> = split(",").filter { it.isNotBlank() }
fun List<String>.toBadgeIdString(): String = joinToString(",")
