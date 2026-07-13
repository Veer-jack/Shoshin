package com.example.shoshinapp.data.models

data class WeekStats(
    val daysCompleted: Int,
    val totalDays: Int = 7,
    val completionRate: Float,
    val dayStatus: Map<String, Boolean>
)

data class MonthStats(
    val checkpointsCompleted: Int,
    val reflectionsWritten: Int,
    val bestDayOfWeek: String,
    val bestHour: Int,
    val completionRate: Float
)

data class AllTimeStats(
    val totalCheckpoints: Int,
    val totalReflections: Int,
    val totalDaysActive: Int,
    val memberSinceDays: Int,
    val groupsJoined: Int,
    val badgesEarned: Int,
    val totalActivations: Int = 0,
    val bestStreak: Int = 0,
    val onTimeRate: String = "0%"
)

data class BestTimes(
    val mostActiveHour: Int,
    val mostActiveDay: String,
    val leastActiveDay: String,
    val personalityType: String
)

data class WeeklyTrend(
    val thisWeekRate: Float,
    val lastWeekRate: Float,
    val direction: String,
    val message: String
)
