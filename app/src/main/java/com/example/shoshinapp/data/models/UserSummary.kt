package com.example.shoshinapp.data.models

data class UserSummary(
    val userId: String,
    val userName: String,
    val profilePictureUrl: String?,
    val currentStreak: Int,
    val totalCheckpoints: Int,
    val badgeCount: Int,
    val activityStatus: String,
    val lastCheckpointDate: Long
)
