package com.example.shoshinapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoshinapp.data.models.UserSummary
import com.example.shoshinapp.data.user.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InviteViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _suggestedFriends = MutableStateFlow<List<UserSummary>>(emptyList())
    val suggestedFriends: StateFlow<List<UserSummary>> = _suggestedFriends.asStateFlow()

    private val _searchResults = MutableStateFlow<List<UserSummary>>(emptyList())
    val searchResults: StateFlow<List<UserSummary>> = _searchResults.asStateFlow()

    init {
        loadSuggestions()
    }

    private fun loadSuggestions() {
        // Mock suggestions
        _suggestedFriends.value = listOf(
            UserSummary("10", "Mike Johnson", null, 15, 87, 4, "Active", 0),
            UserSummary("11", "Sarah Lee", null, 23, 110, 5, "Active", 0),
            UserSummary("12", "Emma Wilson", null, 7, 45, 3, "Building", 0)
        )
    }

    fun searchFriends(query: String) {
        if (query.length < 2) {
            _searchResults.value = emptyList()
            return
        }
        // In real app, search Firestore
        _searchResults.value = _suggestedFriends.value.filter { it.userName.contains(query, ignoreCase = true) }
    }

    fun getUserInviteCode(): String {
        // This would come from UserEntity in a real app
        return "VINAY142"
    }

    fun getInviteLink(): String {
        return "https://shoshin.app/join/${getUserInviteCode()}"
    }
}
