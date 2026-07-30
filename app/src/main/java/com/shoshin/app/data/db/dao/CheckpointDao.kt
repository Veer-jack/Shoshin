package com.Shoshin.app.data.db.dao

import androidx.room.*
import com.Shoshin.app.data.db.entities.CheckpointEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CheckpointDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCheckpoint(checkpoint: CheckpointEntity)

    @Query("SELECT * FROM checkpoints WHERE userId = :userId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLastCheckpoint(userId: String): CheckpointEntity?

    @Query("SELECT * FROM checkpoints WHERE userId = :userId ORDER BY timestamp DESC")
    fun getCheckpointsFlow(userId: String): Flow<List<CheckpointEntity>>

    @Query("DELETE FROM checkpoints WHERE userId = :userId")
    suspend fun clearCheckpoints(userId: String)
}
