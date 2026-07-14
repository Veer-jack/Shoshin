package com.example.shoshinapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoshinapp.data.ReferralRepository
import com.example.shoshinapp.data.UserLimitsRepository
import com.example.shoshinapp.data.models.UserLimits
import com.example.shoshinapp.data.user.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ReferralViewModel(
    private val userRepository: UserRepository,
    private val referralRepository: ReferralRepository,
    private val limitsRepository: UserLimitsRepository
) : ViewModel() {

    private val userId = userRepository.userId

    private val _limits = MutableStateFlow<UserLimits?>(null)
    val limits: StateFlow<UserLimits?> = _limits.asStateFlow()

    init {
        loadLimits()
    }

    private fun loadLimits() {
        val uid = userId ?: return
        viewModelScope.launch {
            limitsRepository.getUserLimits(uid).collect {
                _limits.value = it
            }
        }
        
        // Initial sync
        viewModelScope.launch {
            limitsRepository.syncLimitsFromFirestore(uid)
        }
    }

    fun getShareMessage(): String {
        val code = _limits.value?.referralCode
        if (code.isNullOrEmpty()) {
            return "Join me on Shoshin - the morning habit app that actually works! Download: https://shoshin.app"
        }
        return "Join me on Shoshin and use my referral code $code when you sign up! We both unlock more groups. Download: https://shoshin.app/join/$code"
    }
}
