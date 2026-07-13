package com.example.shoshinapp.data.db.dao

import androidx.room.*
import com.example.shoshinapp.data.db.entities.BadgeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BadgeDao {
    @Query("SELECT * FROM user_badges WHERE userId = :userId")
    fun getBadgesForUser(userId: String): Flow<List<BadgeEntity>>

    @Query("SELECT * FROM user_badges WHERE userId = :userId AND badgeId = :badgeId")
    suspend fun getBadge(userId: String, badgeId: String): BadgeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateBadge(badge: BadgeEntity)

    @Update
    suspend fun updateBadge(badge: BadgeEntity)

    @Query("UPDATE user_badges SET progress = :progress, lastUpdated = :timestamp WHERE userId = :userId AND badgeId = :badgeId")
    suspend fun updateProgress(userId: String, badgeId: String, progress: Int, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE user_badges SET isLocked = 0, unlockedDate = :unlockedDate, lastUpdated = :timestamp WHERE userId = :userId AND badgeId = :badgeId")
    suspend fun unlockBadge(userId: String, badgeId: String, unlockedDate: Long, timestamp: Long = System.currentTimeMillis())
}
