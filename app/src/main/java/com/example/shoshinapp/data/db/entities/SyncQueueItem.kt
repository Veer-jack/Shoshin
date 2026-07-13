package com.example.shoshinapp.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sync_queue")
data class SyncQueueItem(
    @PrimaryKey val id: String,
    val userId: String,
    val entityType: String, // streak, reflection, photo
    val entityId: String,
    val operation: String, // insert, update, delete
    val data: String, // JSON
    val timestamp: Long = System.currentTimeMillis(),
    val retryCount: Int = 0
)
