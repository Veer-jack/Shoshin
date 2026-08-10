package com.Shoshin.app.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Shoshin.app.data.BadgeRepository
import com.Shoshin.app.data.groups.GroupRepository
import com.Shoshin.app.data.user.UserRepository
import com.Shoshin.app.data.db.dao.StreakDao
import com.Shoshin.app.data.db.entities.StreakEntity
import com.Shoshin.app.data.db.entities.UserEntity
import com.Shoshin.app.ui.theme.ShMatchaLightToken
import com.Shoshin.app.utils.AnalyticsManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class StreakViewModel(
    private val userRepository: UserRepository,
    private val badgeRepository: BadgeRepository,
    private val streakDao: StreakDao? = null,
    private val groupRepository: GroupRepository? = null
) : ViewModel() {

    private val _user = MutableStateFlow<UserEntity?>(null)
    val user: StateFlow<UserEntity?> = _user.asStateFlow()

    private val _lastMilestoneReached = MutableStateFlow<Int?>(null)
    val lastMilestoneReached: StateFlow<Int?> = _lastMilestoneReached.asStateFlow()

    private val _newBadgeUnlocked = MutableStateFlow<String?>(null)
    val newBadgeUnlocked: StateFlow<String?> = _newBadgeUnlocked.asStateFlow()

    // This week, Monday-first: null = day hasn't happened yet, true = kept, false = missed.
    private val _weekPattern = MutableStateFlow<List<Boolean?>>(List(7) { null })
    val weekPattern: StateFlow<List<Boolean?>> = _weekPattern.asStateFlow()

    // Last 4 calendar months' completion rate (0f-1f), oldest first.
    private val _monthlyTrend = MutableStateFlow<List<Pair<String, Float>>>(emptyList())
    val monthlyTrend: StateFlow<List<Pair<String, Float>>> = _monthlyTrend.asStateFlow()

    // Full per-day completion log (date "yyyy-MM-dd" -> completed), for calendar/history views.
    private val _historyByDate = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val historyByDate: StateFlow<Map<String, Boolean>> = _historyByDate.asStateFlow()

    init {
        loadUser()
        checkFreezeReset()
        loadHistory()
    }

    private fun loadHistory() {
        val uid = userRepository.userId ?: return
        val dao = streakDao ?: return
        viewModelScope.launch {
            dao.getUserStreaksFlow(uid).collect { entries ->
                _historyByDate.value = entries.associate { it.date to it.completed }
                val completedDates = entries.filter { it.completed }.map { it.date }.toSet()
                val dateFmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

                // Week pattern: Monday..Sunday of the current week.
                val cal = Calendar.getInstance()
                cal.firstDayOfWeek = Calendar.MONDAY
                while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) cal.add(Calendar.DAY_OF_YEAR, -1)
                val todayStr = dateFmt.format(Date())
                val week = (0 until 7).map { offset ->
                    val dayCal = cal.clone() as Calendar
                    dayCal.add(Calendar.DAY_OF_YEAR, offset)
                    val dayStr = dateFmt.format(dayCal.time)
                    when {
                        dayStr > todayStr -> null // hasn't happened yet
                        completedDates.contains(dayStr) -> true
                        else -> false
                    }
                }
                _weekPattern.value = week

                // Monthly trend: last 4 calendar months, oldest first.
                val monthFmt = SimpleDateFormat("yyyy-MM", Locale.getDefault())
                val monthLabelFmt = SimpleDateFormat("MMM", Locale.getDefault())
                val monthCal = Calendar.getInstance()
                monthCal.add(Calendar.MONTH, -3)
                val trend = (0 until 4).map {
                    val key = monthFmt.format(monthCal.time)
                    val label = monthLabelFmt.format(monthCal.time)
                    val daysInMonth = monthCal.getActualMaximum(Calendar.DAY_OF_MONTH)
                    val keptThisMonth = completedDates.count { it.startsWith(key) }
                    val rate = (keptThisMonth.toFloat() / daysInMonth.toFloat()).coerceIn(0f, 1f)
                    monthCal.add(Calendar.MONTH, 1)
                    label to rate
                }
                _monthlyTrend.value = trend
            }
        }
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

    fun saveRoutineProgress(stepIndex: Int) {
        val currentUser = _user.value ?: return
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        viewModelScope.launch {
            userRepository.updateUser(currentUser.copy(
                lastRoutineStepIndex = stepIndex,
                lastRoutineDate = today
            ))
        }
    }

    fun resetRoutineProgress() {
        val currentUser = _user.value ?: return
        viewModelScope.launch {
            userRepository.updateUser(currentUser.copy(
                lastRoutineStepIndex = 0,
                lastRoutineDate = ""
            ))
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

        // Record today's completion for the per-day history (weekly/monthly charts, calendar).
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(now))
        viewModelScope.launch {
            streakDao?.insertStreak(
                StreakEntity(
                    streakId = "$uid-$today",
                    userId = uid,
                    date = today,
                    completed = true,
                    timestamp = now,
                    syncStatus = "pending",
                    lastUpdated = now
                )
            )
        }

        // Reflect the new streak on this user's row in every group they belong to.
        viewModelScope.launch {
            groupRepository?.syncMemberStatsToAllGroups(uid, newStreak, today)
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
            days >= 8 -> ShMatchaLightToken // Green
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
