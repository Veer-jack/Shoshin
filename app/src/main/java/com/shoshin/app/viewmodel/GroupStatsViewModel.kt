package com.Shoshin.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Shoshin.app.data.groups.GroupRepository
import com.Shoshin.app.data.models.GroupStats
import com.Shoshin.app.data.models.UserSummary
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GroupStatsViewModel(
    private val repository: GroupRepository
) : ViewModel() {

    private val _members = MutableStateFlow<List<UserSummary>>(emptyList())
    val members: StateFlow<List<UserSummary>> = _members.asStateFlow()

    private val _stats = MutableStateFlow<GroupStats?>(null)
    val stats: StateFlow<GroupStats?> = _stats.asStateFlow()

    fun loadGroupData(groupId: String) {
        viewModelScope.launch {
            val groupResult = repository.getGroupDetails(groupId)
            val membersResult = repository.getGroupMembers(groupId)

            val group = groupResult.getOrNull()
            val groupMembers = membersResult.getOrNull() ?: emptyList()

            val summaries = groupMembers.map {
                UserSummary(
                    userId = it.userId,
                    userName = it.name,
                    profilePictureUrl = null,
                    currentStreak = it.consistencyStreak,
                    totalCheckpoints = it.checkpointsCompleted,
                    badgeCount = 0, // not tracked per group member yet
                    activityStatus = if (it.consistencyStreak > 0) "Active" else "Inactive",
                    lastCheckpointDate = 0L // not tracked per group member yet
                )
            }
            _members.value = summaries

            val groupAgeInDays = group?.createdAt?.let {
                ((System.currentTimeMillis() - it.time) / (1000 * 60 * 60 * 24)).toInt().coerceAtLeast(0)
            } ?: 0

            _stats.value = GroupStats(
                groupId = groupId,
                activeMembersThisWeek = groupMembers.count { it.consistencyStreak > 0 },
                totalMemberCount = groupMembers.size,
                averageStreak = if (groupMembers.isNotEmpty()) groupMembers.map { it.consistencyStreak }.average() else 0.0,
                totalCheckpointsThisMonth = groupMembers.sumOf { it.checkpointsCompleted },
                groupAgeInDays = groupAgeInDays,
                topPerformers = summaries.sortedByDescending { it.currentStreak }.take(3),
                lastUpdated = System.currentTimeMillis()
            )
        }
    }
}
