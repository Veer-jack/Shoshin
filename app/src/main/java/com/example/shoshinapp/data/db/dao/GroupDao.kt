package com.example.shoshinapp.data.db.dao

import androidx.room.*
import com.example.shoshinapp.data.db.entities.GroupEntity

@Dao
interface GroupDao {
    @Insert
    suspend fun insertGroup(group: GroupEntity)
    
    @Update
    suspend fun updateGroup(group: GroupEntity)
    
    @Query("SELECT * FROM groups")
    suspend fun getAllGroups(): List<GroupEntity>
    
    @Query("SELECT * FROM groups WHERE groupId = :groupId")
    suspend fun getGroup(groupId: String): GroupEntity?
    
    @Query("SELECT * FROM groups WHERE userId = :userId")
    suspend fun getUserGroups(userId: String): List<GroupEntity>
    
    @Query("SELECT * FROM groups WHERE syncStatus = 'pending'")
    suspend fun getPendingGroups(): List<GroupEntity>
}
