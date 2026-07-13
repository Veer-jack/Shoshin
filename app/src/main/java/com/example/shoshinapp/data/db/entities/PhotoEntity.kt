package com.example.shoshinapp.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photos")
data class PhotoEntity(
    @PrimaryKey val photoId: String,
    val userId: String,
    val localCompressedPath: String?,
    val firebaseUrl: String?,
    val date: String,
    val timestamp: Long,
    val syncStatus: String = "pending",
    val lastUpdated: Long = System.currentTimeMillis()
)
