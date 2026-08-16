package com.Shoshin.app.data.models

data class MonthlyStats(
    val daysCompleted: Int,
    val completionRate: Float,
    val currentStreak: Int,
    val bestStreak: Int
)
