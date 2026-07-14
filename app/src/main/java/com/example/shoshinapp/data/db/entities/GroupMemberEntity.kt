package com.example.shoshinapp.data.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "group_members",
    foreignKeys = [
        ForeignKey(
            entity = GroupEntity::class,
            parentColumns = ["groupId"],
            childColumns = ["groupId"]
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"]
        )
    ]
)
data class GroupMemberEntity(
    @PrimaryKey val memberId: String = java.util.UUID.randomUUID().toString(),
    val groupId: String,
    val userId: String,
    val role: String,           // "admin", "member"
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val totalCheckpoints: Int = 0,
    val joinedAt: Long = System.currentTimeMillis(),
    val syncStatus: String = "synced"
)
