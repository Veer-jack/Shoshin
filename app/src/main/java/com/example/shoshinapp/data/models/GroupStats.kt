package com.example.shoshinapp.data.models

data class GroupStats(
    val groupId: String,
    val activeMembersThisWeek: Int,
    val totalMemberCount: Int,
    val averageStreak: Double,
    val totalCheckpointsThisMonth: Int,
    val groupAgeInDays: Int,
    val topPerformers: List<UserSummary>,
    val lastUpdated: Long
)
