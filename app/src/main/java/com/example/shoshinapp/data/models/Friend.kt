package com.example.shoshinapp.data.models

data class Friend(
    val friendId: String,
    val userId: String,
    val userName: String,
    val profilePictureUrl: String?,
    val currentStreak: Int,
    val bestStreak: Int,
    val followedDate: Long,
    val lastCheckpointDate: Long,
    val activityStatus: String // Active, Building, Inactive
)
