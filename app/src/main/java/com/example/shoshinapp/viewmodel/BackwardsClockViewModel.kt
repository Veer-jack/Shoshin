package com.example.shoshinapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoshinapp.data.user.UserRepository
import com.example.shoshinapp.features.clock.DaySettings
import com.example.shoshinapp.features.clock.TimeRemaining
import com.example.shoshinapp.features.clock.calculateTimeRemaining
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class BackwardsClockViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _daySettings = MutableStateFlow(DaySettings())
    val daySettings: StateFlow<DaySettings> = _daySettings.asStateFlow()

    private val _timeRemaining = MutableStateFlow(calculateTimeRemaining(_daySettings.value))
    val timeRemaining: StateFlow<TimeRemaining> = _timeRemaining.asStateFlow()

    private var tickerJob: Job? = null
    private val formatter = DateTimeFormatter.ofPattern("HH:mm")

    init {
        observeUserSettings()
        startClock()
    }

    private fun observeUserSettings() {
        val uid = userRepository.userId ?: return
        viewModelScope.launch {
            userRepository.getUserFlow(uid).collect { user ->
                if (user != null) {
                    try {
                        val start = LocalTime.parse(user.productiveStartTime, formatter)
                        val end = LocalTime.parse(user.productiveEndTime, formatter)
                        _daySettings.value = DaySettings(start, end)
                    } catch (e: Exception) {
                        // Fallback to defaults on parse error
                    }
                }
            }
        }
    }

    private fun startClock() {
        tickerJob?.cancel()
        tickerJob = viewModelScope.launch {
            while (isActive) {
                _timeRemaining.value = calculateTimeRemaining(_daySettings.value)
                delay(1000)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        tickerJob?.cancel()
    }
}
