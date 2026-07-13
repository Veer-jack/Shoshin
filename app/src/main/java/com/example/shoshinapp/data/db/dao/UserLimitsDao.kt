package com.example.shoshinapp.data.db.dao

import androidx.room.*
import com.example.shoshinapp.data.db.entities.UserLimitsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserLimitsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserLimits(limits: UserLimitsEntity)

    @Query("SELECT * FROM user_limits WHERE userId = :userId")
    fun getUserLimits(userId: String): Flow<UserLimitsEntity?>

    @Query("UPDATE user_limits SET groupsJoinLimit = :newLimit, lastUpdated = :timestamp WHERE userId = :userId")
    suspend fun updateGroupsJoinLimit(userId: String, newLimit: Int, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE user_limits SET groupMemberLimit = :newLimit, lastUpdated = :timestamp WHERE userId = :userId")
    suspend fun updateGroupMemberLimit(userId: String, newLimit: Int, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE user_limits SET totalReferrals = totalReferrals + 1, lastUpdated = :timestamp WHERE userId = :userId")
    suspend fun incrementReferralCount(userId: String, timestamp: Long = System.currentTimeMillis())

    @Query("SELECT * FROM user_limits WHERE referralCode = :code")
    suspend fun getUserByReferralCode(code: String): UserLimitsEntity?

    @Query("UPDATE user_limits SET groupsJoinLimit = :groupsJoinLimit, groupMemberLimit = :groupMemberLimit, lastUpdated = :timestamp WHERE userId = :userId")
    suspend fun updateLimits(userId: String, groupsJoinLimit: Int, groupMemberLimit: Int, timestamp: Long = System.currentTimeMillis())
}
