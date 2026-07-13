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
    @PrimaryKey val memberId: String,
    val groupId: String,
    val userId: String,
    val role: String,           // "admin", "member"
    val currentStreak: Int,
    val bestStreak: Int,
    val totalCheckpoints: Int,
    val joinedAt: Long,
    val syncStatus: String
)
