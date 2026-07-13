package com.example.shoshinapp.data.db.dao

import androidx.room.*
import com.example.shoshinapp.data.db.entities.FriendEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FriendDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriend(friend: FriendEntity)

    @Query("SELECT * FROM friends WHERE currentUserId = :userId ORDER BY friendStreak DESC")
    fun getAllFriendsForUser(userId: String): Flow<List<FriendEntity>>

    @Query("SELECT * FROM friends WHERE userId = :friendUserId AND currentUserId = :userId")
    suspend fun getFriendById(userId: String, friendUserId: String): FriendEntity?

    @Query("DELETE FROM friends WHERE userId = :friendUserId AND currentUserId = :userId")
    suspend fun deleteFriend(userId: String, friendUserId: String)

    @Query("UPDATE friends SET friendStreak = :streak, lastCheckpointDate = :lastDate WHERE userId = :friendUserId AND currentUserId = :userId")
    suspend fun updateFriendStreak(userId: String, friendUserId: String, streak: Int, lastDate: Long)

    @Query("SELECT * FROM friends WHERE currentUserId = :userId ORDER BY friendStreak DESC LIMIT :limit")
    suspend fun getTopFriendsByStreak(userId: String, limit: Int): List<FriendEntity>

    @Query("SELECT * FROM friends WHERE currentUserId = :userId AND friendName LIKE '%' || :query || '%'")
    suspend fun searchFriends(userId: String, query: String): List<FriendEntity>
}
