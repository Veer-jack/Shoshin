package com.example.shoshinapp.data.models

data class UserLimits(
    val userId: String,
    val groupsJoinLimit: Int = 5,
    val groupMemberLimit: Int = 5,
    val totalReferrals: Int = 0,
    val referralCode: String,
    val referredByUserId: String? = null,
    val createdDate: Long = System.currentTimeMillis(),
    val lastUpdated: Long = System.currentTimeMillis()
)
