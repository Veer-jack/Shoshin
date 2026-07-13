package com.example.shoshinapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoshinapp.data.groups.Group
import com.example.shoshinapp.data.groups.GroupMember
import com.example.shoshinapp.data.groups.GroupRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GroupViewModel : ViewModel() {
    private val repository = GroupRepository()

    private val _groups = MutableStateFlow<List<Group>>(emptyList())
    val groups: StateFlow<List<Group>> = _groups.asStateFlow()

    private val _currentGroup = MutableStateFlow<Group?>(null)
    val currentGroup: StateFlow<Group?> = _currentGroup.asStateFlow()

    private val _groupMembers = MutableStateFlow<List<GroupMember>>(emptyList())
    val groupMembers: StateFlow<List<GroupMember>> = _groupMembers.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _groupPosts = MutableStateFlow<List<com.example.shoshinapp.data.db.entities.GroupPostEntity>>(emptyList())
    val groupPosts: StateFlow<List<com.example.shoshinapp.data.db.entities.GroupPostEntity>> = _groupPosts.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _limitReached = MutableStateFlow<String?>(null)
    val limitReached: StateFlow<String?> = _limitReached.asStateFlow()

    private val _groupFull = MutableStateFlow<String?>(null)
    val groupFull: StateFlow<String?> = _groupFull.asStateFlow()

    fun loadGroups() {
        // ...
    }
    
    // ... (rest of methods)

    fun joinGroup(inviteCode: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.joinGroup(inviteCode)
            result.onSuccess {
                loadGroups()
            }
            result.onFailure { 
                val msg = it.message ?: ""
                when {
                    msg.startsWith("LIMIT_REACHED:") -> _limitReached.value = msg.substringAfter(":")
                    msg.startsWith("GROUP_FULL:") -> _groupFull.value = msg.substringAfter(":")
                    else -> _error.value = msg
                }
            }
            _isLoading.value = false
        }
    }

    fun clearLimitError() {
        _limitReached.value = null
        _groupFull.value = null
    }

    fun loadGroupMembers(groupId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            // Load group details too
            val groupResult = repository.getGroupDetails(groupId)
            groupResult.onSuccess { _currentGroup.value = it }

            val result = repository.getGroupMembers(groupId)
            result.onSuccess { _groupMembers.value = it }
            result.onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun leaveGroup(groupId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.leaveGroup(groupId)
            result.onSuccess {
                loadGroups()
            }
            result.onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun deleteGroup(groupId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.deleteGroup(groupId)
            result.onSuccess {
                loadGroups()
            }
            result.onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun loadGroupPosts(groupId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getGroupPosts(groupId)
            result.onSuccess { _groupPosts.value = it }
            result.onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun postToGroup(groupId: String, userId: String, content: String, photoUrl: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.postToGroup(groupId, userId, content, photoUrl)
            result.onSuccess {
                loadGroupPosts(groupId)
            }
            result.onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }
}
