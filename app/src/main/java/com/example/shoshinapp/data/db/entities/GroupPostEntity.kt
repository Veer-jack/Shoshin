package com.example.shoshinapp.data.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "group_posts",
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
data class GroupPostEntity(
    @PrimaryKey val postId: String,
    val groupId: String,
    val userId: String,
    val content: String,        // reflection text
    val photoUrl: String?,      // photo URL
    val likes: Int,
    val createdAt: Long,
    val syncStatus: String
)
