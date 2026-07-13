package com.example.shoshinapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoshinapp.data.db.dao.*
import com.example.shoshinapp.data.models.*
import com.example.shoshinapp.data.user.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class StatsViewModel(
    private val statsDao: StatsDao,
    private val userDao: UserDao,
    private val badgeDao: BadgeDao,
    private val userRepository: UserRepository
) : ViewModel() {

    private val userId = userRepository.userId

    private val _allTimeStats = MutableStateFlow<AllTimeStats?>(null)
    val allTimeStats: StateFlow<AllTimeStats?> = _allTimeStats.asStateFlow()

    private val _heatmapData = MutableStateFlow<Map<String, Int>>(emptyMap())
    val heatmapData: StateFlow<Map<String, Int>> = _heatmapData.asStateFlow()

    init {
        loadStats()
        loadHeatmap()
    }

    private fun loadStats() {
        // ... (existing code)
    }

    private fun loadHeatmap() {
        val uid = userId ?: return
        val sixMonthsAgo = System.currentTimeMillis() - (180L * 24 * 60 * 60 * 1000)
        viewModelScope.launch {
            statsDao.getHeatmapData(uid, sixMonthsAgo).collect { checkpoints ->
                val map = checkpoints.groupBy { 
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(it.timestamp))
                }.mapValues { it.value.size }
                _heatmapData.value = map
            }
        }
    }
}
