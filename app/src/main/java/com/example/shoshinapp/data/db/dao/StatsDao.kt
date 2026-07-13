package com.example.shoshinapp.data.db.dao

import androidx.room.*
import com.example.shoshinapp.data.db.entities.CheckpointEntity
import com.example.shoshinapp.data.db.entities.ReflectionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StatsDao {
    @Query("SELECT * FROM checkpoints WHERE userId = :userId AND timestamp BETWEEN :startDate AND :endDate")
    suspend fun getCheckpointsByDateRange(userId: String, startDate: Long, endDate: Long): List<CheckpointEntity>

    @Query("SELECT COUNT(*) FROM checkpoints WHERE userId = :userId")
    fun getTotalCheckpoints(userId: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM reflections WHERE userId = :userId")
    fun getTotalReflections(userId: String): Flow<Int>

    @Query("SELECT * FROM checkpoints WHERE userId = :userId ORDER BY timestamp DESC")
    fun getAllCheckpoints(userId: String): Flow<List<CheckpointEntity>>

    @Query("SELECT COUNT(*) FROM checkpoints WHERE userId = :userId AND timestamp >= :weekStart")
    suspend fun getCheckpointCountSince(userId: String, weekStart: Long): Int

    @Query("SELECT * FROM checkpoints WHERE userId = :userId AND timestamp >= :startDate")
    fun getHeatmapData(userId: String, startDate: Long): Flow<List<CheckpointEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCheckpoint(checkpoint: CheckpointEntity)
}
