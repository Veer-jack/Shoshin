package com.example.shoshinapp.data.groups

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Group(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val createdBy: String = "",
    val members: List<String> = emptyList(),
    val inviteCode: String = "",
    @ServerTimestamp
    val createdAt: Date? = null
)

data class GroupMember(
    val userId: String = "",
    val name: String = "",
    val consistencyStreak: Int = 0,
    val activations: Int = 0,
    val checkpointsCompleted: Int = 0,
    @ServerTimestamp
    val joinedAt: Date? = null
)

data class GroupInvite(
    val code: String = "",
    val groupId: String = "",
    @ServerTimestamp
    val createdAt: Date? = null,
    val maxUses: Int = 0,
    val timesUsed: Int = 0,
    val expiresIn: Long = 604800000 // 7 days in milliseconds
)
