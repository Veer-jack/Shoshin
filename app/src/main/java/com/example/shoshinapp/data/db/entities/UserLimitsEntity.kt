package com.example.shoshinapp.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_limits")
data class UserLimitsEntity(
    @PrimaryKey val userId: String,
    val groupsJoinLimit: Int = 5,
    val groupMemberLimit: Int = 5,
    val totalReferrals: Int = 0,
    val referralCode: String,
    val referredByUserId: String? = null,
    val lastUpdated: Long = System.currentTimeMillis()
)
