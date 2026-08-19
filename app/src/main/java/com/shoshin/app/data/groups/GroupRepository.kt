package com.Shoshin.app.data.groups

import android.graphics.Bitmap
import com.Shoshin.app.R
import com.Shoshin.app.data.db.dao.GroupDao
import com.Shoshin.app.data.db.dao.GroupMemberDao
import com.Shoshin.app.data.db.dao.NotificationDao
import com.Shoshin.app.data.db.entities.GroupEntity
import com.Shoshin.app.data.db.entities.GroupMemberEntity
import com.Shoshin.app.data.db.entities.NotificationEntity
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.UUID

class GroupRepository(
    private val groupDao: GroupDao,
    private val memberDao: GroupMemberDao,
    private val notificationDao: NotificationDao? = null
) {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance()

    /** Uploads [bitmap] as the group's avatar and returns its download URL. */
    private suspend fun uploadGroupPhoto(groupId: String, bitmap: Bitmap): String {
        val ref = storage.reference.child("groups/$groupId/avatar.jpg")
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, baos)
        ref.putBytes(baos.toByteArray()).await()
        return ref.downloadUrl.await().toString()
    }

    /** Local-only echo of the current user's own group action — not seen by other members. */
    private suspend fun notifyLocal(title: String, body: String) {
        val uid = auth.currentUser?.uid ?: return
        notificationDao?.insertNotification(
            NotificationEntity(
                notificationId = UUID.randomUUID().toString(),
                userId = uid,
                type = "social",
                title = title,
                body = body,
                iconRes = R.drawable.ic_groups,
                timestamp = System.currentTimeMillis(),
                isRead = false,
                syncStatus = "synced"
            )
        )
    }

    suspend fun createGroup(name: String, description: String, photoBitmap: Bitmap? = null): Result<String> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(Exception("Not authenticated"))
            val groupId = UUID.randomUUID().toString()
            val inviteCode = UUID.randomUUID().toString().take(6).uppercase()

            // Upload before the group doc exists so the URL can be written in the same .set() call
            // below, rather than creating the group first and patching the photo on afterward.
            val photoUrl = photoBitmap?.let { uploadGroupPhoto(groupId, it) }

            val group = Group(
                id = groupId,
                name = name,
                description = description,
                createdBy = userId,
                members = listOf(userId),
                inviteCode = inviteCode,
                photo = photoUrl
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
                photo = photoUrl,
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

            notifyLocal("You created $name", "Your circle is ready. Invite others to join.")
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
                photo = group.photo,
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

            notifyLocal("You joined ${group.name}", "Welcome to the circle.")
            Result.success(groupId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getGroups(): Result<List<Group>> {
        val userId = auth.currentUser?.uid ?: return Result.failure(Exception("Not authenticated"))
        return getGroupsForUser(userId, syncLocal = true)
    }

    /** Same query as [getGroups] but for any user, not just the current one (e.g. "groups in common" lookups). */
    suspend fun getGroupsForUser(userId: String, syncLocal: Boolean = false): Result<List<Group>> {
        return try {
            val query = db.collection("groups").whereArrayContains("members", userId).get().await()
            val groups = query.documents.mapNotNull { it.toObject(Group::class.java) }

            if (syncLocal) {
                groups.forEach { g ->
                    val entity = GroupEntity(
                        groupId = g.id,
                        userId = g.createdBy,
                        groupName = g.name,
                        description = g.description,
                        memberCount = g.members.size,
                        photo = g.photo,
                        inviteLinkCode = g.inviteCode,
                        created_at = System.currentTimeMillis(),
                        updated_at = System.currentTimeMillis(),
                        syncStatus = "synced"
                    )
                    val existing = groupDao.getGroup(g.id)
                    if (existing == null) groupDao.insertGroup(entity) else groupDao.updateGroup(entity)
                }
            }

            Result.success(groups)
        } catch (e: Exception) {
            if (!syncLocal) return Result.failure(e)
            // If error (e.g. offline), return local groups
            val localGroups = groupDao.getAllGroups().map { entity ->
                Group(id = entity.groupId, name = entity.groupName, description = entity.description, createdBy = entity.userId, inviteCode = entity.inviteLinkCode, members = emptyList(), photo = entity.photo)
            }
            if (localGroups.isNotEmpty()) Result.success(localGroups) else Result.failure(e)
        }
    }

    /**
     * Adds [targetUserId] directly to [groupId] (no invite/accept step exists in this app).
     * Mirrors [joinGroup]'s limit checks: the target's own join cap and the group's member cap.
     */
    suspend fun addMemberToGroup(groupId: String, targetUserId: String, targetName: String): Result<Unit> {
        return try {
            val targetLimitsSnapshot = db.collection("users").document(targetUserId).collection("limits").document("current").get().await()
            val groupsJoinLimit = targetLimitsSnapshot.getLong("groupsJoinLimit")?.toInt() ?: 5
            val targetGroupsQuery = db.collection("groups").whereArrayContains("members", targetUserId).get().await()
            if (targetGroupsQuery.size() >= groupsJoinLimit) {
                return Result.failure(Exception("LIMIT_REACHED:This member has reached their limit of $groupsJoinLimit groups."))
            }

            val groupRef = db.collection("groups").document(groupId)
            val groupDoc = groupRef.get().await()
            val group = groupDoc.toObject(Group::class.java) ?: return Result.failure(Exception("Group not found"))

            if (group.members.contains(targetUserId)) {
                return Result.failure(Exception("Already a member of this group"))
            }

            val creatorLimitsSnapshot = db.collection("users").document(group.createdBy).collection("limits").document("current").get().await()
            val groupMemberLimit = creatorLimitsSnapshot.getLong("groupMemberLimit")?.toInt() ?: 5
            if (group.members.size >= groupMemberLimit) {
                return Result.failure(Exception("GROUP_FULL:This group has reached its maximum of $groupMemberLimit members."))
            }

            val newMembers = group.members + targetUserId
            groupRef.update("members", newMembers).await()
            groupRef.collection("members").document(targetUserId).set(
                GroupMember(userId = targetUserId, name = targetName)
            ).await()

            val entity = groupDao.getGroup(groupId)
            if (entity != null) {
                groupDao.updateGroup(entity.copy(memberCount = newMembers.size, updated_at = System.currentTimeMillis()))
            }

            notifyLocal("You added $targetName to ${group.name}", "They can now see it in their groups list.")
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getGroupByInviteCode(code: String): Result<Group> {
        return try {
            val query = db.collection("groups").whereEqualTo("inviteCode", code).get().await()
            if (query.documents.isEmpty()) return Result.failure(Exception("Invalid invite code"))
            val group = query.documents.first().toObject(Group::class.java) ?: return Result.failure(Exception("Invalid group"))
            Result.success(group)
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

    /** Pushes a user's latest streak/completion to every group they belong to. */
    suspend fun syncMemberStatsToAllGroups(userId: String, streak: Int, completionDate: String) {
        try {
            val query = db.collection("groups").whereArrayContains("members", userId).get().await()
            for (doc in query.documents) {
                doc.reference.collection("members").document(userId).update(
                    mapOf(
                        "consistencyStreak" to streak,
                        "lastCompletionDate" to completionDate,
                        "checkpointsCompleted" to FieldValue.increment(1)
                    )
                ).await()
            }
        } catch (e: Exception) {
            android.util.Log.e("GroupRepo", "Failed to sync member stats", e)
        }
    }

    /** Live leaderboard: emits an updated, streak-sorted member list on every Firestore change. */
    fun getGroupMembersFlow(groupId: String): Flow<List<GroupMember>> = callbackFlow {
        val registration = db.collection("groups").document(groupId).collection("members")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    android.util.Log.e("GroupRepo", "Leaderboard listener error", error)
                    return@addSnapshotListener
                }
                val members = snapshot?.documents?.mapNotNull { it.toObject(GroupMember::class.java) }
                    ?.sortedByDescending { it.consistencyStreak } ?: emptyList()
                trySend(members)
            }
        awaitClose { registration.remove() }
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

            // Remove from local cache
            groupDao.deleteGroup(groupId)

            notifyLocal("You left ${group.name}", "You can rejoin anytime with the invite code.")
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

            // Delete the avatar image, if any. Not fatal — the group is already gone either way.
            if (group.photo != null) {
                try {
                    storage.reference.child("groups/$groupId/avatar.jpg").delete().await()
                } catch (e: StorageException) {
                    android.util.Log.w("GroupRepo", "Failed to delete group avatar for $groupId", e)
                }
            }

            // Remove from local cache
            groupDao.deleteGroup(groupId)

            notifyLocal("You deleted ${group.name}", "The circle and its members have been removed.")
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** Creator-only: removes a member from the group. */
    suspend fun removeMember(groupId: String, targetUserId: String): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(Exception("Not authenticated"))

            val groupRef = db.collection("groups").document(groupId)
            val groupDoc = groupRef.get().await()
            val group = groupDoc.toObject(Group::class.java) ?: return Result.failure(Exception("Group not found"))

            if (group.createdBy != userId) {
                return Result.failure(Exception("Only the creator can remove members"))
            }

            val newMembers = group.members.filter { it != targetUserId }
            groupRef.update("members", newMembers).await()
            groupRef.collection("members").document(targetUserId).delete().await()

            notifyLocal("You removed a member from ${group.name}", "They can rejoin later with a new invite code.")
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** Creator-only: updates the group's name/description. */
    suspend fun updateGroupDetails(groupId: String, name: String, description: String): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(Exception("Not authenticated"))

            val groupRef = db.collection("groups").document(groupId)
            val groupDoc = groupRef.get().await()
            val group = groupDoc.toObject(Group::class.java) ?: return Result.failure(Exception("Group not found"))

            if (group.createdBy != userId) {
                return Result.failure(Exception("Only the creator can edit the circle"))
            }

            groupRef.update(mapOf("name" to name, "description" to description)).await()

            val existing = groupDao.getGroup(groupId)
            if (existing != null) {
                groupDao.updateGroup(existing.copy(groupName = name, description = description, updated_at = System.currentTimeMillis()))
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** Creator-only: replaces the group's avatar image. */
    suspend fun updateGroupPhoto(groupId: String, bitmap: Bitmap): Result<String> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(Exception("Not authenticated"))

            val groupRef = db.collection("groups").document(groupId)
            val groupDoc = groupRef.get().await()
            val group = groupDoc.toObject(Group::class.java) ?: return Result.failure(Exception("Group not found"))

            if (group.createdBy != userId) {
                return Result.failure(Exception("Only the creator can change the circle image"))
            }

            val url = uploadGroupPhoto(groupId, bitmap)
            groupRef.update("photo", url).await()

            val existing = groupDao.getGroup(groupId)
            if (existing != null) {
                groupDao.updateGroup(existing.copy(photo = url, updated_at = System.currentTimeMillis()))
            }

            Result.success(url)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** Creator-only: invalidates the current invite code and generates a new one. */
    suspend fun regenerateInviteCode(groupId: String): Result<String> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(Exception("Not authenticated"))

            val groupRef = db.collection("groups").document(groupId)
            val groupDoc = groupRef.get().await()
            val group = groupDoc.toObject(Group::class.java) ?: return Result.failure(Exception("Group not found"))

            if (group.createdBy != userId) {
                return Result.failure(Exception("Only the creator can regenerate the invite code"))
            }

            val newCode = UUID.randomUUID().toString().take(6).uppercase()
            groupRef.update("inviteCode", newCode).await()

            val existing = groupDao.getGroup(groupId)
            if (existing != null) {
                groupDao.updateGroup(existing.copy(inviteLinkCode = newCode, updated_at = System.currentTimeMillis()))
            }

            Result.success(newCode)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getGroupPosts(groupId: String): Result<List<com.Shoshin.app.data.db.entities.GroupPostEntity>> {
        return try {
            val query = db.collection("groups").document(groupId).collection("posts").orderBy("createdAt").get().await()
            val posts = query.documents.mapNotNull { it.toObject(com.Shoshin.app.data.db.entities.GroupPostEntity::class.java) }
            Result.success(posts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun postToGroup(groupId: String, userId: String, content: String, photoUrl: String?): Result<Unit> {
        return try {
            val postId = UUID.randomUUID().toString()
            val post = com.Shoshin.app.data.db.entities.GroupPostEntity(
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
