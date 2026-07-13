package com.example.shoshinapp.data.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "social_shares",
    foreignKeys = [
        ForeignKey(
            entity = GroupPostEntity::class,
            parentColumns = ["postId"],
            childColumns = ["postId"]
        )
    ]
)
data class SocialShareEntity(
    @PrimaryKey val shareId: String,
    val postId: String,
    val userId: String,
    val platform: String,       // "instagram", "whatsapp", "snapchat", "facebook", "twitter"
    val shareUrl: String?,      // deep link if applicable
    val isSuccessful: Boolean,
    val sharedAt: Long,
    val syncStatus: String
)
