package com.example.shoshinapp.data.db.dao

import androidx.room.*
import com.example.shoshinapp.data.db.entities.StreakEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StreakDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStreak(streak: StreakEntity)

    @Query("SELECT * FROM streaks WHERE userId = :userId ORDER BY timestamp DESC")
    fun getUserStreaksFlow(userId: String): Flow<List<StreakEntity>>

    @Query("SELECT * FROM streaks WHERE userId = :userId ORDER BY timestamp DESC")
    suspend fun getUserStreaks(userId: String): List<StreakEntity>

    @Query("SELECT * FROM streaks WHERE userId = :userId AND date = :date")
    suspend fun getStreakByDate(userId: String, date: String): StreakEntity?

    @Query("SELECT * FROM streaks WHERE streakId = :streakId")
    suspend fun getStreak(streakId: String): StreakEntity?

    @Update
    suspend fun updateStreak(streak: StreakEntity)

    @Delete
    suspend fun deleteStreak(streak: StreakEntity)

    @Query("SELECT * FROM streaks WHERE syncStatus = 'pending' AND userId = :userId")
    suspend fun getPendingStreaks(userId: String): List<StreakEntity>
}
