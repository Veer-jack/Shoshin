package com.example.shoshinapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoshinapp.data.models.GroupStats
import com.example.shoshinapp.data.models.UserSummary
import com.example.shoshinapp.data.db.dao.GroupDao
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GroupStatsViewModel(
    private val groupDao: GroupDao
) : ViewModel() {

    private val _members = MutableStateFlow<List<UserSummary>>(emptyList())
    val members: StateFlow<List<UserSummary>> = _members.asStateFlow()

    private val _stats = MutableStateFlow<GroupStats?>(null)
    val stats: StateFlow<GroupStats?> = _stats.asStateFlow()

    fun loadGroupData(groupId: String) {
        // In a real app, this would fetch from a repository that syncs with Firestore
        // For now, mock some members for the specification
        _members.value = listOf(
            UserSummary("1", "Sarah", null, 31, 142, 8, "Active today", System.currentTimeMillis()),
            UserSummary("2", "John", null, 23, 110, 5, "Active today", System.currentTimeMillis()),
            UserSummary("3", "Emma", null, 15, 88, 4, "Active today", System.currentTimeMillis()),
            UserSummary("4", "Mike", null, 12, 45, 3, "Not yet today", System.currentTimeMillis() - 86400000),
            UserSummary("5", "Alex", null, 8, 22, 2, "Missed yesterday", System.currentTimeMillis() - 86400000 * 2)
        )
        
        _stats.value = GroupStats(
            groupId = groupId,
            activeMembersThisWeek = 6,
            totalMemberCount = 8,
            averageStreak = 18.0,
            totalCheckpointsThisMonth = 142,
            groupAgeInDays = 60,
            topPerformers = _members.value.take(3),
            lastUpdated = System.currentTimeMillis()
        )
    }
}
