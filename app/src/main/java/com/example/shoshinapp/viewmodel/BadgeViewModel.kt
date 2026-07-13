package com.example.shoshinapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoshinapp.data.BadgeRepository
import com.example.shoshinapp.data.models.Badge
import com.example.shoshinapp.data.user.UserRepository
import com.example.shoshinapp.utils.AnalyticsManager
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
