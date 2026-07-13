package com.example.shoshinapp.data.db.dao

import androidx.room.*
import com.example.shoshinapp.data.db.entities.GroupMemberEntity

@Dao
interface GroupMemberDao {
    @Insert
    suspend fun insertMember(member: GroupMemberEntity)
    
    @Query("SELECT * FROM group_members WHERE groupId = :groupId")
    suspend fun getGroupMembers(groupId: String): List<GroupMemberEntity>
    
    @Query("SELECT * FROM group_members WHERE userId = :userId")
    suspend fun getUserGroupMemberships(userId: String): List<GroupMemberEntity>
}
