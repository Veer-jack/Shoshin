package com.Shoshin.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Shoshin.app.data.models.GroupStats
import com.Shoshin.app.data.models.UserSummary
import com.Shoshin.app.data.db.dao.GroupDao
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
        // Dummy data removed. Real data will be loaded when repositories are linked.
        _members.value = emptyList()
        _stats.value = null
    }
}
