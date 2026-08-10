package com.Shoshin.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.Shoshin.app.data.db.entities.RoutineCheckpointEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineCheckpointDao {
    @Query("SELECT * FROM routine_checkpoints WHERE userId = :userId AND templateKey = :templateKey ORDER BY displayOrder ASC")
    fun getRoutineFlow(userId: String, templateKey: String): Flow<List<RoutineCheckpointEntity>>

    @Query("SELECT * FROM routine_checkpoints WHERE userId = :userId AND templateKey = :templateKey ORDER BY displayOrder ASC")
    suspend fun getRoutine(userId: String, templateKey: String): List<RoutineCheckpointEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(checkpoints: List<RoutineCheckpointEntity>)
}
