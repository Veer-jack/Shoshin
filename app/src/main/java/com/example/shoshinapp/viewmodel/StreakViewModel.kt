package com.example.shoshinapp.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoshinapp.data.BadgeRepository
import com.example.shoshinapp.data.user.UserRepository
import com.example.shoshinapp.data.db.entities.UserEntity
import com.example.shoshinapp.ui.theme.ShMatcha
import com.example.shoshinapp.utils.AnalyticsManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class StreakViewModel(
    private val userRepository: UserRepository,
    private val badgeRepository: BadgeRepository
) : ViewModel() {

    private val _user = MutableStateFlow<UserEntity?>(null)
    val user: StateFlow<UserEntity?> = _user.asStateFlow()

    private val _lastMilestoneReached = MutableStateFlow<Int?>(null)
    val lastMilestoneReached: StateFlow<Int?> = _lastMilestoneReached.asStateFlow()

    private val _newBadgeUnlocked = MutableStateFlow<String?>(null)
    val newBadgeUnlocked: StateFlow<String?> = _newBadgeUnlocked.asStateFlow()

    init {
        loadUser()
        checkFreezeReset()
    }

    private fun loadUser() {
        val uid = userRepository.userId ?: return
        viewModelScope.launch {
            userRepository.getUserFlow(uid).collect {
                _user.value = it
            }
        }
    }

    private fun checkFreezeReset() {
        viewModelScope.launch {
            val uid = userRepository.userId ?: return@launch
            val currentUser = userRepository.getUser(uid) ?: return@launch
            val now = System.currentTimeMillis()
            
            // Reset freezes every 30 days
            if (now - currentUser.lastFreezeResetDate > 30L * 24 * 60 * 60 * 1000) {
                userRepository.updateUser(currentUser.copy(
                    freezesUsedThisMonth = 0,
                    lastFreezeResetDate = now
                ))
            }
        }
    }

    fun incrementStreak() {
        val currentUser = _user.value ?: return
        val now = System.currentTimeMillis()
        val uid = currentUser.userId
        
        // Basic check: only increment once per day
        if (isSameDay(currentUser.lastCheckpointDate, now)) return

        val newStreak = currentUser.currentStreak + 1
        val newBest = if (newStreak > currentUser.bestStreak) newStreak else currentUser.bestStreak
        val startDate = if (currentUser.currentStreak == 0) now else currentUser.streakStartDate

        // Check for milestones
        var newFreezes = currentUser.streakFreezes
        if (newStreak == 30) newFreezes += 1
        if (newStreak == 60) newFreezes += 1
        if (newStreak == 100) newFreezes += 1

        if (newStreak in listOf(7, 30, 100, 365)) {
            _lastMilestoneReached.value = newStreak
            checkStreakBadges(uid, newStreak)
            AnalyticsManager.logMilestoneReached(newStreak, "professional")
        }

        AnalyticsManager.logStreakUpdated(newStreak, currentUser.currentStreak, "professional")

        // Beginner badge on first ever activation
        if (currentUser.totalActivations == 0) {
            viewModelScope.launch {
                badgeRepository.unlockBadge(uid, "milestone_first")
                _newBadgeUnlocked.value = "Beginner"
            }
        }

        viewModelScope.launch {
            userRepository.updateUser(currentUser.copy(
                currentStreak = newStreak,
                bestStreak = newBest,
                streakStartDate = startDate,
                lastCheckpointDate = now,
                totalActivations = currentUser.totalActivations + 1,
                streakFreezes = newFreezes
            ))
        }
    }

    private fun checkStreakBadges(userId: String, streak: Int) {
        val badgeId = when (streak) {
            7 -> "streak_7"
            30 -> "streak_30"
            100 -> "streak_100"
            365 -> "streak_365"
            else -> null
        }
        
        badgeId?.let { id ->
            val name = if (streak == 7) "Starter" else if (streak == 30) "Committed" else if (streak == 100) "Legend" else "Immortal"
            viewModelScope.launch {
                badgeRepository.unlockBadge(userId, id)
                _newBadgeUnlocked.value = name
                AnalyticsManager.logBadgeUnlocked(id, name, "streak", "professional")
            }
        }
    }

    fun useStreakFreeze() {
        val currentUser = _user.value ?: return
        if (currentUser.freezesUsedThisMonth < currentUser.streakFreezes) {
            AnalyticsManager.logStreakFreezeUsed(currentUser.currentStreak)
            viewModelScope.launch {
                userRepository.updateUser(currentUser.copy(
                    freezesUsedThisMonth = currentUser.freezesUsedThisMonth + 1,
                    lastCheckpointDate = System.currentTimeMillis() // Mark as done for today
                ))
            }
        }
    }

    fun clearMilestone() {
        _lastMilestoneReached.value = null
    }

    fun clearBadgeUnlock() {
        _newBadgeUnlocked.value = null
    }

    fun resetStreak() {
        val currentUser = _user.value ?: return
        viewModelScope.launch {
            userRepository.updateUser(currentUser.copy(
                currentStreak = 0,
                streakStartDate = 0
            ))
        }
    }

    fun getStreakColor(days: Int): Color {
        return when {
            days >= 100 -> Color(0xFFE91E63) // Pink
            days >= 31 -> Color(0xFFFF9800)  // Orange
            days >= 8 -> ShMatcha           // Green
            else -> Color(0xFFFFC107)        // Yellow
        }
    }

    fun getMilestoneBadges(days: Int): List<String> {
        val badges = mutableListOf<String>()
        if (days >= 7) badges.add("🥈")
        if (days >= 30) badges.add("🥇")
        if (days >= 100) badges.add("👑")
        if (days >= 365) badges.add("⭐")
        return badges
    }

    private fun isSameDay(t1: Long, t2: Long): Boolean {
        val cal1 = Calendar.getInstance().apply { timeInMillis = t1 }
        val cal2 = Calendar.getInstance().apply { timeInMillis = t2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }
}
