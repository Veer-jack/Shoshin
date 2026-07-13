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
    val created_at: Long,
    val updated_at: Long,
    val syncStatus: String      // "pending", "synced"
)
