package com.example.shoshinapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoshinapp.data.user.UserRepository
import com.example.shoshinapp.data.ShoshinRepository
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val userRepository: UserRepository,
    private val shoshinRepository: ShoshinRepository
) : ViewModel() {

    fun completeOnboarding(startTime: String, endTime: String) {
        val uid = userRepository.userId ?: return
        viewModelScope.launch {
            try {
                userRepository.getUser(uid)?.let { user ->
                    userRepository.updateUser(user.copy(
                        onboardingCompleted = true,
                        productiveStartTime = startTime,
                        productiveEndTime = endTime
                    ))
                }
            } finally {
                shoshinRepository.completeOnboarding()
            }
        }
    }

    fun skipOnboarding() {
        val uid = userRepository.userId ?: return
        viewModelScope.launch {
            try {
                userRepository.getUser(uid)?.let { user ->
                    userRepository.updateUser(user.copy(onboardingCompleted = true))
                }
            } finally {
                shoshinRepository.completeOnboarding()
            }
        }
    }
}
