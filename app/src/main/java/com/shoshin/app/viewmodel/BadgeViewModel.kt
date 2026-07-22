package com.shoshin.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shoshin.app.data.BadgeRepository
import com.shoshin.app.data.models.Badge
import com.shoshin.app.data.user.UserRepository
import com.shoshin.app.utils.AnalyticsManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class BadgeViewModel(
    private val badgeRepository: BadgeRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    val userId = userRepository.userId

    private val _badges = MutableStateFlow<List<Badge>>(emptyList())
    val badges: StateFlow<List<Badge>> = _badges.asStateFlow()

    init {
        loadBadges()
    }

    private fun loadBadges() {
        val uid = userId ?: return
        viewModelScope.launch {
            badgeRepository.getBadgesForUser(uid).collect {
                _badges.value = it
            }
        }
    }

    fun unlockBadge(badgeId: String) {
        val uid = userId ?: return
        viewModelScope.launch {
            badgeRepository.unlockBadge(uid, badgeId)
        }
    }
}
