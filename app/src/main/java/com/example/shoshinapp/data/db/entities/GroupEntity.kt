package com.example.shoshinapp.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "groups")
data class GroupEntity(
    @PrimaryKey val groupId: String,
    val userId: String,         // creator
    val groupName: String,
    val description: String,
    val memberCount: Int,
    val photo: String?,
    val activeMembersCount: Int = 0,
    val averageStreak: Double = 0.0,
    val totalCheckpointsThisMonth: Int = 0,
    val inviteLinkCode: String = "",
    val lastStatsUpdate: Long = 0,
    val created_at: Long,
    val updated_at: Long,
    val syncStatus: String      // "pending", "synced"
)
