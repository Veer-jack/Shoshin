package com.example.shoshinapp.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reflections")
data class ReflectionEntity(
    @PrimaryKey val reflectionId: String,
    val userId: String,
    val content: String,
    val date: String,
    val timestamp: Long,
    val syncStatus: String = "pending", // pending, synced, failed
    val version: Long = 1,
    val lastUpdated: Long = System.currentTimeMillis()
)
