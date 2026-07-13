package com.example.shoshinapp.data.db.dao

import androidx.room.*
import com.example.shoshinapp.data.db.entities.GroupPostEntity

@Dao
interface GroupPostDao {
    @Insert
    suspend fun insertPost(post: GroupPostEntity)
    
    @Query("SELECT * FROM group_posts WHERE groupId = :groupId ORDER BY createdAt DESC")
    suspend fun getGroupPosts(groupId: String): List<GroupPostEntity>
    
    @Query("SELECT * FROM group_posts WHERE syncStatus = 'pending'")
    suspend fun getPendingPosts(): List<GroupPostEntity>
    
    @Update
    suspend fun updatePost(post: GroupPostEntity)
}
