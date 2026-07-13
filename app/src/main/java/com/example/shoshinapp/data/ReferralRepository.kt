package com.example.shoshinapp.data

import android.util.Log
import com.example.shoshinapp.data.db.dao.UserLimitsDao
import com.example.shoshinapp.data.db.entities.UserLimitsEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.*

class ReferralRepository(
    private val userLimitsDao: UserLimitsDao,
    private val firestore: FirebaseFirestore
) {

    suspend fun generateAndSaveReferralCode(userId: String, displayName: String): String {
        var code = ""
        var isUnique = false
        
        val base = displayName.uppercase()
            .filter { it.isLetter() }
            .let { if (it.length >= 5) it.substring(0, 5) else it.padEnd(5, 'X') }

        while (!isUnique) {
            val random = Random().nextInt(1000).toString().padEnd(3, '0')
            code = "$base$random"
            
            val snapshot = firestore.collection("referralCodes").document(code).get().await()
            if (!snapshot.exists()) {
                isUnique = true
            }
        }

        // Save to Firestore top-level collection for fast lookup
        firestore.collection("referralCodes").document(code).set(
            mapOf("userId" to userId, "createdDate" to System.currentTimeMillis())
        ).await()

        // Also save to user's limits document
        firestore.collection("users").document(userId).collection("limits").document("current")
            .update("referralCode", code).await()

        return code
    }

    suspend fun validateReferralCode(code: String): String? {
        return try {
            val snapshot = firestore.collection("referralCodes").document(code.uppercase()).get().await()
            if (snapshot.exists()) {
                snapshot.getString("userId")
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun deliverReferralReward(referrerId: String, newUserId: String) {
        try {
            // 1. Update Referrer in Firestore
            val referrerRef = firestore.collection("users").document(referrerId).collection("limits").document("current")
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(referrerRef)
                val currentJoinLimit = snapshot.getLong("groupsJoinLimit") ?: 5
                val currentMemberLimit = snapshot.getLong("groupMemberLimit") ?: 5
                val currentReferrals = snapshot.getLong("totalReferrals") ?: 0

                transaction.update(referrerRef, mapOf(
                    "groupsJoinLimit" to currentJoinLimit + 5,
                    "groupMemberLimit" to currentMemberLimit + 5,
                    "totalReferrals" to currentReferrals + 1,
                    "lastUpdated" to com.google.firebase.Timestamp.now()
                ))
            }.await()

            // 2. Initial Limits for New User (starts at 10/10 because they used a code)
            val newUserLimits = UserLimitsEntity(
                userId = newUserId,
                groupsJoinLimit = 10,
                groupMemberLimit = 10,
                totalReferrals = 0,
                referralCode = "", // Will be generated separately
                referredByUserId = referrerId
            )
            
            firestore.collection("users").document(newUserId).collection("limits").document("current").set(
                mapOf(
                    "groupsJoinLimit" to 10,
                    "groupMemberLimit" to 10,
                    "totalReferrals" to 0,
                    "referredByUserId" to referrerId,
                    "lastUpdated" to com.google.firebase.Timestamp.now()
                )
            ).await()

            // 3. Update Local DB for new user (if already logged in)
            userLimitsDao.insertUserLimits(newUserLimits)
            
            Log.d("Referral", "Reward delivered for referrer $referrerId and new user $newUserId")
        } catch (e: Exception) {
            Log.e("Referral", "Error delivering reward", e)
        }
    }
}
