package com.example.shoshinapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoshinapp.data.FriendRepository
import com.example.shoshinapp.data.models.Friend
import com.example.shoshinapp.data.user.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FriendStreaksViewModel(
    private val friendRepository: FriendRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _topFriends = MutableStateFlow<List<Friend>>(emptyList())
    val topFriends: StateFlow<List<Friend>> = _topFriends.asStateFlow()

    private val _allFriends = MutableStateFlow<List<Friend>>(emptyList())
    val allFriends: StateFlow<List<Friend>> = _allFriends.asStateFlow()

    init {
        loadFriends()
    }

    private fun loadFriends() {
        val uid = userRepository.userId ?: return
        viewModelScope.launch {
            friendRepository.getAllFriends(uid).collect { list ->
                _allFriends.value = list
                _topFriends.value = list.sortedByDescending { it.currentStreak }.take(5)
            }
        }
    }

    fun removeFriend(friendUserId: String) {
        val uid = userRepository.userId ?: return
        viewModelScope.launch {
            friendRepository.removeFriend(uid, friendUserId)
        }
    }
}
