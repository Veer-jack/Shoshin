package com.Shoshin.app.data.db.entities

import androidx.room.Entity

@Entity(tableName = "routine_checkpoints", primaryKeys = ["userId", "templateKey", "slotIndex"])
data class RoutineCheckpointEntity(
    val userId: String,
    val templateKey: String,
    val slotIndex: Int,
    val label: String,
    val displayOrder: Int,
    val updatedAt: Long = System.currentTimeMillis()
)
