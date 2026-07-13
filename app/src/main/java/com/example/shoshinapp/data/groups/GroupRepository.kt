package com.example.shoshinapp.data.groups

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import java.util.UUID

class GroupRepository {
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

            db.collection("groups").document(groupId).set(group).await()
            
            // Add creator as member
            db.collection("groups").document(groupId).collection("members").document(userId).set(
                GroupMember(userId = userId, name = "You")
            ).await()

            Result.success(groupId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun joinGroup(inviteCode: String): Result<String> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(Exception("Not authenticated"))

            // Find group by invite code
            val query = db.collection("groups").whereEqualTo("inviteCode", inviteCode).get().await()
            
            if (query.documents.isEmpty()) {
                return Result.failure(Exception("Invalid invite code"))
            }

            val groupDoc = query.documents.first()
            val groupId = groupDoc.id
            val group = groupDoc.toObject(Group::class.java) ?: return Result.failure(Exception("Invalid group"))

            // Check if user already member
            if (group.members.contains(userId)) {
                return Result.failure(Exception("Already member of this group"))
            }

            // Add user to members list
            val newMembers = group.members + userId
            db.collection("groups").document(groupId).update("members", newMembers).await()

            // Add as group member
            db.collection("groups").document(groupId).collection("members").document(userId).set(
                GroupMember(userId = userId, name = "Member")
            ).await()

            Result.success(groupId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getGroups(): Result<List<Group>> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(Exception("Not authenticated"))
            
            val query = db.collection("groups").whereArrayContains("members", userId).get().await()
            val groups = query.documents.mapNotNull { it.toObject(Group::class.java) }
            
            Result.success(groups)
        } catch (e: Exception) {
            Result.failure(e)
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

            // Remove from members list
            val newMembers = group.members.filter { it != userId }
            groupRef.update("members", newMembers).await()

            // Remove from group members collection
            groupRef.collection("members").document(userId).delete().await()

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

            // Delete members subcollection
            val membersQuery = groupRef.collection("members").get().await()
            for (doc in membersQuery.documents) {
                doc.reference.delete().await()
            }

            // Delete group
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
