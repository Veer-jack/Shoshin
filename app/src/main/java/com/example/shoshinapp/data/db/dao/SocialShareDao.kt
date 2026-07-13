package com.example.shoshinapp.data.db.dao

import androidx.room.*
import com.example.shoshinapp.data.db.entities.SocialShareEntity

@Dao
interface SocialShareDao {
    @Insert
    suspend fun insertShare(share: SocialShareEntity)
    
    @Query("SELECT * FROM social_shares WHERE postId = :postId")
    suspend fun getPostShares(postId: String): List<SocialShareEntity>
    
    @Query("SELECT * FROM social_shares WHERE userId = :userId ORDER BY sharedAt DESC")
    suspend fun getUserShares(userId: String): List<SocialShareEntity>
    
    @Query("SELECT * FROM social_shares WHERE syncStatus = 'pending'")
    suspend fun getPendingShares(): List<SocialShareEntity>
    
    @Update
    suspend fun updateShare(share: SocialShareEntity)
}
