package com.example.shoshinapp.data.db.dao

import androidx.room.*
import com.example.shoshinapp.data.db.entities.ReflectionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReflectionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReflection(reflection: ReflectionEntity)

    @Query("SELECT * FROM reflections WHERE userId = :userId ORDER BY timestamp DESC")
    fun getUserReflectionsFlow(userId: String): Flow<List<ReflectionEntity>>

    @Query("SELECT * FROM reflections WHERE userId = :userId ORDER BY timestamp DESC")
    suspend fun getUserReflections(userId: String): List<ReflectionEntity>

    @Query("SELECT * FROM reflections WHERE reflectionId = :id")
    suspend fun getReflection(id: String): ReflectionEntity?

    @Update
    suspend fun updateReflection(reflection: ReflectionEntity)

    @Delete
    suspend fun deleteReflection(reflection: ReflectionEntity)

    @Query("SELECT * FROM reflections WHERE syncStatus = 'pending' AND userId = :userId")
    suspend fun getPendingReflections(userId: String): List<ReflectionEntity>
}
