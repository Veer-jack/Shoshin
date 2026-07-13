package com.example.shoshinapp.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "checkpoints")
data class CheckpointEntity(
    @PrimaryKey val checkpointId: String,
    val userId: String,
    val timestamp: Long,
    val hourOfDay: Int,
    val dayOfWeek: String,
    val locationLat: Double? = null,
    val locationLong: Double? = null,
    val syncStatus: String = "pending"
)
