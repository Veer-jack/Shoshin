package com.example.shoshinapp.data.groups

import com.example.shoshinapp.data.db.dao.GroupDao
import com.example.shoshinapp.data.db.dao.GroupMemberDao
import com.example.shoshinapp.data.db.entities.GroupEntity
import com.example.shoshinapp.data.db.entities.GroupMemberEntity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import java.util.UUID

class GroupRepository(
    private val groupDao: GroupDao,
    private val memberDao: GroupMemberDao
) {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun createGroup(name: String, description: String): Result<String> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(Exception("Not authenticated"))
            val groupId = UUID.randomUUID().toString()
            val inviteCode = UUID.randomUUID().toString().take(6).uppercase()

            val group = Group(
                id = groupId,
                name = name,
                description = description,
                createdBy = userId,
                members = listOf(userId),
                inviteCode = inviteCode
            )

            // Save to Firestore
            android.util.Log.d("GroupRepo", "Saving group to Firestore...")
            db.collection("groups").document(groupId).set(group).await()
            
            // Add creator as member in Firestore
            android.util.Log.d("GroupRepo", "Adding creator as member in Firestore...")
            db.collection("groups").document(groupId).collection("members").document(userId).set(
                GroupMember(userId = userId, name = "You")
            ).await()

            // Save to Local DB
            android.util.Log.d("GroupRepo", "Saving group to Local DB...")
            val groupEntity = GroupEntity(
                groupId = groupId,
                userId = userId,
                groupName = name,
                description = description,
                memberCount = 1,
                photo = null,
                inviteLinkCode = inviteCode,
                created_at = System.currentTimeMillis(),
                updated_at = System.currentTimeMillis(),
                syncStatus = "synced"
            )
            groupDao.insertGroup(groupEntity)
            
            android.util.Log.d("GroupRepo", "Saving member to Local DB...")
            memberDao.insertMember(GroupMemberEntity(
                groupId = groupId,
                userId = userId,
                role = "creator",
                joinedAt = System.currentTimeMillis()
            ))

            Result.success(groupId)
        } catch (e: Exception) {
            android.util.Log.e("GroupRepo", "Failed to create group", e)
            Result.failure(e)
        }
    }

    suspend fun joinGroup(inviteCode: String): Result<String> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(Exception("Not authenticated"))

            // 1. Check User's Join Limit
            val userLimitsSnapshot = db.collection("users").document(userId).collection("limits").document("current").get().await()
            val groupsJoinLimit = userLimitsSnapshot.getLong("groupsJoinLimit")?.toInt() ?: 5
            
            val userGroupsQuery = db.collection("groups").whereArrayContains("members", userId).get().await()
            if (userGroupsQuery.size() >= groupsJoinLimit) {
                return Result.failure(Exception("LIMIT_REACHED:You can join up to $groupsJoinLimit groups. Refer a friend to unlock more!"))
            }

            // 2. Find group by invite code
            val query = db.collection("groups").whereEqualTo("inviteCode", inviteCode).get().await()
            
            if (query.documents.isEmpty()) {
                return Result.failure(Exception("Invalid invite code"))
            }

            val groupDoc = query.documents.first()
            val groupId = groupDoc.id
            val group = groupDoc.toObject(Group::class.java) ?: return Result.failure(Exception("Invalid group"))

            // 3. Check Group's Member Limit (based on creator)
            val creatorLimitsSnapshot = db.collection("users").document(group.createdBy).collection("limits").document("current").get().await()
            val groupMemberLimit = creatorLimitsSnapshot.getLong("groupMemberLimit")?.toInt() ?: 5
            
            if (group.members.size >= groupMemberLimit) {
                return Result.failure(Exception("GROUP_FULL:This group has reached its maximum of $groupMemberLimit members."))
            }

            // Check if user already member
            if (group.members.contains(userId)) {
                return Result.failure(Exception("Already member of this group"))
            }

            // Add user to members list in Firestore
            val newMembers = group.members + userId
            db.collection("groups").document(groupId).update("members", newMembers).await()

            // Add as group member in Firestore
            db.collection("groups").document(groupId).collection("members").document(userId).set(
                GroupMember(userId = userId, name = "Member")
            ).await()

            // Save to Local DB
            val entity = GroupEntity(
                groupId = group.id,
                userId = group.createdBy,
                groupName = group.name,
                description = group.description,
                memberCount = newMembers.size,
                photo = null,
                inviteLinkCode = group.inviteCode,
                created_at = System.currentTimeMillis(),
                updated_at = System.currentTimeMillis(),
                syncStatus = "synced"
            )
            val existing = groupDao.getGroup(group.id)
            if (existing == null) groupDao.insertGroup(entity) else groupDao.updateGroup(entity)

            memberDao.insertMember(GroupMemberEntity(
                groupId = groupId,
                userId = userId,
                role = "member",
                joinedAt = System.currentTimeMillis()
            ))

            Result.success(groupId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getGroups(): Result<List<Group>> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(Exception("Not authenticated"))
            
            // Fetch from Firestore
            val query = db.collection("groups").whereArrayContains("members", userId).get().await()
            val groups = query.documents.mapNotNull { it.toObject(Group::class.java) }
            
            // Sync with local DB
            groups.forEach { g ->
                val entity = GroupEntity(
                    groupId = g.id,
                    userId = g.createdBy,
                    groupName = g.name,
                    description = g.description,
                    memberCount = g.members.size,
                    photo = null,
                    inviteLinkCode = g.inviteCode,
                    created_at = System.currentTimeMillis(),
                    updated_at = System.currentTimeMillis(),
                    syncStatus = "synced"
                )
                val existing = groupDao.getGroup(g.id)
                if (existing == null) groupDao.insertGroup(entity) else groupDao.updateGroup(entity)
            }
            
            Result.success(groups)
        } catch (e: Exception) {
            // If error (e.g. offline), return local groups
            val localGroups = groupDao.getAllGroups().map { entity ->
                Group(id = entity.groupId, name = entity.groupName, description = entity.description, createdBy = entity.userId, inviteCode = entity.inviteLinkCode, members = emptyList())
            }
            if (localGroups.isNotEmpty()) Result.success(localGroups) else Result.failure(e)
        }
    }

    suspend fun getGroupDetails(groupId: String): Result<Group> {
        return try {
            val doc = db.collection("groups").document(groupId).get().await()
            val group = doc.toObject(Group::class.java) ?: return Result.failure(Exception("Group not found"))
            Result.success(group)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getGroupMembers(groupId: String): Result<List<GroupMember>> {
        return try {
            val query = db.collection("groups").document(groupId).collection("members").get().await()
            val members = query.documents.mapNotNull { it.toObject(GroupMember::class.java) }
            Result.success(members.sortedByDescending { it.consistencyStreak })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateMemberStats(groupId: String, userId: String, streak: Int, activations: Int): Result<Unit> {
        return try {
            db.collection("groups").document(groupId).collection("members").document(userId).update(
                "consistencyStreak", streak,
                "activations", activations
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun leaveGroup(groupId: String): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(Exception("Not authenticated"))
            
            val groupRef = db.collection("groups").document(groupId)
            val groupDoc = groupRef.get().await()
            val group = groupDoc.toObject(Group::class.java) ?: return Result.failure(Exception("Group not found"))

            // Remove from members list in Firestore
            val newMembers = group.members.filter { it != userId }
            groupRef.update("members", newMembers).await()

            // Remove from group members collection in Firestore
            groupRef.collection("members").document(userId).delete().await()

            // Remove from local DB - ideally we should have a deleteGroup or similar
            // For now, we don't have a DAO method to delete a group by ID easily without the entity
            // but we can just leave it there or add a method to DAO.

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteGroup(groupId: String): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(Exception("Not authenticated"))
            
            val groupRef = db.collection("groups").document(groupId)
            val groupDoc = groupRef.get().await()
            val group = groupDoc.toObject(Group::class.java) ?: return Result.failure(Exception("Group not found"))

            // Check if user is creator
            if (group.createdBy != userId) {
                return Result.failure(Exception("Only creator can delete group"))
            }

            // Delete members subcollection in Firestore
            val membersQuery = groupRef.collection("members").get().await()
            for (doc in membersQuery.documents) {
                doc.reference.delete().await()
            }

            // Delete group in Firestore
            groupRef.delete().await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getGroupPosts(groupId: String): Result<List<com.example.shoshinapp.data.db.entities.GroupPostEntity>> {
        return try {
            val query = db.collection("groups").document(groupId).collection("posts").orderBy("createdAt").get().await()
            val posts = query.documents.mapNotNull { it.toObject(com.example.shoshinapp.data.db.entities.GroupPostEntity::class.java) }
            Result.success(posts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun postToGroup(groupId: String, userId: String, content: String, photoUrl: String?): Result<Unit> {
        return try {
            val postId = UUID.randomUUID().toString()
            val post = com.example.shoshinapp.data.db.entities.GroupPostEntity(
                postId = postId,
                groupId = groupId,
                userId = userId,
                content = content,
                photoUrl = photoUrl,
                likes = 0,
                createdAt = System.currentTimeMillis(),
                syncStatus = "pending"
            )
            db.collection("groups").document(groupId).collection("posts").document(postId).set(post).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
