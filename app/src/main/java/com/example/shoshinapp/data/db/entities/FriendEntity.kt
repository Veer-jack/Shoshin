package com.example.shoshinapp.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "friends")
data class FriendEntity(
    @PrimaryKey val friendId: String, // The unique ID of the friendship relationship
    val currentUserId: String, // The user who owns this friend record
    val userId: String, // The ID of the friend user
    val friendName: String,
    val friendProfilePicture: String?,
    val friendStreak: Int,
    val friendBestStreak: Int,
    val followedDate: Long,
    val lastCheckpointDate: Long,
    val activityStatus: String,
    val lastUpdated: Long = System.currentTimeMillis()
)
