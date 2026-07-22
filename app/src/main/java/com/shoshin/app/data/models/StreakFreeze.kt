package com.shoshin.app.data.models

data class StreakFreeze(
    val freezesAvailable: Int,
    val freezesUsed: Int,
    val nextResetDate: Long
)
