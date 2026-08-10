package com.Shoshin.app.data

import com.Shoshin.app.data.db.dao.RoutineCheckpointDao
import com.Shoshin.app.data.db.entities.RoutineCheckpointEntity
import com.Shoshin.app.data.routine.RoutineDefinitions
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class RoutineRepository(
    private val dao: RoutineCheckpointDao,
    private val firestore: FirebaseFirestore
) {

    fun getRoutineFlow(userId: String, templateKey: String): Flow<List<RoutineCheckpointEntity>> =
        dao.getRoutineFlow(userId, templateKey)

    suspend fun ensureSeeded(userId: String, templateKey: String) {
        if (dao.getRoutine(userId, templateKey).isNotEmpty()) return
        val defaults = RoutineDefinitions.forTemplate(templateKey)
        val seeded = defaults.mapIndexed { index, def ->
            RoutineCheckpointEntity(
                userId = userId,
                templateKey = templateKey,
                slotIndex = index,
                label = def.label,
                displayOrder = index
            )
        }
        dao.insertAll(seeded)
    }

    suspend fun saveRoutine(userId: String, templateKey: String, checkpoints: List<RoutineCheckpointEntity>) {
        dao.insertAll(checkpoints)
        try {
            val sorted = checkpoints.sortedBy { it.displayOrder }
            firestore.collection("users").document(userId)
                .collection("routines").document(templateKey)
                .set(
                    mapOf(
                        "checkpoints" to sorted.map { mapOf("slotIndex" to it.slotIndex, "label" to it.label, "displayOrder" to it.displayOrder) },
                        "updatedAt" to System.currentTimeMillis()
                    )
                ).await()
        } catch (e: Exception) {
            android.util.Log.e("RoutineRepository", "Cloud sync failed: ${e.message}")
        }
    }
}
