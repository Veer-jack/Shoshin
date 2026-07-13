package com.example.shoshinapp.features.clock

import java.time.LocalTime
import java.time.LocalDateTime
import java.time.Duration
import kotlin.math.max

data class DaySettings(
    val productiveStartTime: LocalTime = LocalTime.of(6, 0),
    val productiveEndTime: LocalTime = LocalTime.of(22, 0),
)

data class TimeRemaining(
    val totalSecondsInDay: Long,
    val secondsRemainingInDay: Long,
    val secondsRemainingProductive: Long,
    val percentDayRemaining: Float,
    val percentProductiveRemaining: Float,
    val hoursRemaining: Int,
    val minutesRemaining: Int,
    val secondsRemaining: Int,
    val productiveHoursRemaining: Float,
    val isProductiveHours: Boolean
)

fun calculateTimeRemaining(settings: DaySettings): TimeRemaining {
    val now = LocalDateTime.now()
    val currentTime = now.toLocalTime()
    val dayEndTime = LocalTime.MAX // 23:59:59.999999999
    
    val totalSecondsInDay = 24 * 3600L
    
    // Seconds from now until end of day
    val secondsRemainingInDay = Duration.between(currentTime, dayEndTime).seconds
    
    // Calculate productive hours remaining
    val isProductiveHours = !currentTime.isBefore(settings.productiveStartTime) && 
                            currentTime.isBefore(settings.productiveEndTime)
    
    val secondsRemainingProductive = if (isProductiveHours) {
        Duration.between(currentTime, settings.productiveEndTime).seconds
    } else {
        0L
    }
    
    val productiveHoursRemaining = secondsRemainingProductive / 3600f
    
    // Calculate percentages
    val percentDayRemaining = (secondsRemainingInDay.toFloat() / totalSecondsInDay) * 100
    
    val productiveDurationSeconds = Duration.between(settings.productiveStartTime, settings.productiveEndTime).seconds
    val percentProductiveRemaining = if (productiveDurationSeconds > 0) {
        (secondsRemainingProductive.toFloat() / productiveDurationSeconds) * 100
    } else {
        0f
    }
    
    // Breakdown for display
    val hoursRem = (secondsRemainingInDay / 3600).toInt()
    val minutesRem = ((secondsRemainingInDay % 3600) / 60).toInt()
    val secondsRem = (secondsRemainingInDay % 60).toInt()
    
    return TimeRemaining(
        totalSecondsInDay = totalSecondsInDay,
        secondsRemainingInDay = secondsRemainingInDay,
        secondsRemainingProductive = secondsRemainingProductive,
        percentDayRemaining = percentDayRemaining,
        percentProductiveRemaining = percentProductiveRemaining,
        hoursRemaining = hoursRem,
        minutesRemaining = minutesRem,
        secondsRemaining = secondsRem,
        productiveHoursRemaining = productiveHoursRemaining,
        isProductiveHours = isProductiveHours
    )
}
