package com.example.shoshinapp.data

import com.example.shoshinapp.data.db.dao.FriendDao
import com.example.shoshinapp.data.db.entities.FriendEntity
import com.example.shoshinapp.data.models.Friend
import com.example.shoshinapp.data.models.FriendRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.*

class FriendRepository(
    private val friendDao: FriendDao,
    private val firestore: FirebaseFirestore
) {

    fun getAllFriends(userId: String): Flow<List<Friend>> {
        return friendDao.getAllFriendsForUser(userId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    suspend fun getTopFriends(userId: String, limit: Int): List<Friend> {
        return friendDao.getTopFriendsByStreak(userId, limit).map { it.toDomainModel() }
    }

    suspend fun addFriend(currentUserId: String, friendUser: Friend): Boolean {
        return try {
            val entity = FriendEntity(
                friendId = "${currentUserId}_${friendUser.userId}",
                currentUserId = currentUserId,
                userId = friendUser.userId,
                friendName = friendUser.userName,
                friendProfilePicture = friendUser.profilePictureUrl,
                friendStreak = friendUser.currentStreak,
                friendBestStreak = friendUser.bestStreak,
                followedDate = System.currentTimeMillis(),
                lastCheckpointDate = friendUser.lastCheckpointDate,
                activityStatus = friendUser.activityStatus
            )
            friendDao.insertFriend(entity)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun removeFriend(currentUserId: String, friendUserId: String): Boolean {
        return try {
            friendDao.deleteFriend(currentUserId, friendUserId)
            // Also remove from Firestore here
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun searchFriends(currentUserId: String, query: String): List<Friend> {
        return friendDao.searchFriends(currentUserId, query).map { it.toDomainModel() }
    }

    // --- Extension functions ---
    private fun FriendEntity.toDomainModel() = Friend(
        friendId = friendId,
        userId = userId,
        userName = friendName,
        profilePictureUrl = friendProfilePicture,
        currentStreak = friendStreak,
        bestStreak = friendBestStreak,
        followedDate = followedDate,
        lastCheckpointDate = lastCheckpointDate,
        activityStatus = activityStatus
    )
}
