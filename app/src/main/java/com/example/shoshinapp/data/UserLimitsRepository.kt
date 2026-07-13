package com.example.shoshinapp.data

import com.example.shoshinapp.data.db.dao.UserLimitsDao
import com.example.shoshinapp.data.db.entities.UserLimitsEntity
import com.example.shoshinapp.data.models.UserLimits
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class UserLimitsRepository(
    private val userLimitsDao: UserLimitsDao,
    private val firestore: FirebaseFirestore
) {

    fun getUserLimits(userId: String): Flow<UserLimits?> {
        return userLimitsDao.getUserLimits(userId).map { entity ->
            entity?.toDomainModel()
        }
    }

    suspend fun syncLimitsFromFirestore(userId: String) {
        try {
            val snapshot = firestore.collection("users").document(userId)
                .collection("limits").document("current").get().await()
            
            if (snapshot.exists()) {
                val entity = UserLimitsEntity(
                    userId = userId,
                    groupsJoinLimit = snapshot.getLong("groupsJoinLimit")?.toInt() ?: 5,
                    groupMemberLimit = snapshot.getLong("groupMemberLimit")?.toInt() ?: 5,
                    totalReferrals = snapshot.getLong("totalReferrals")?.toInt() ?: 0,
                    referralCode = snapshot.getString("referralCode") ?: "",
                    referredByUserId = snapshot.getString("referredByUserId")
                )
                userLimitsDao.insertUserLimits(entity)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun UserLimitsEntity.toDomainModel() = UserLimits(
        userId = userId,
        groupsJoinLimit = groupsJoinLimit,
        groupMemberLimit = groupMemberLimit,
        totalReferrals = totalReferrals,
        referralCode = referralCode,
        referredByUserId = referredByUserId
    )
}
