package com.Shoshin.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Shoshin.app.data.db.entities.UserEntity
import com.Shoshin.app.data.groups.Group
import com.Shoshin.app.data.groups.GroupMember
import com.Shoshin.app.data.groups.GroupRepository
import com.Shoshin.app.data.user.UserRepository
import com.Shoshin.app.utils.AnalyticsManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GroupViewModel(
    private val repository: GroupRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _groups = MutableStateFlow<List<Group>>(emptyList())
    val groups: StateFlow<List<Group>> = _groups.asStateFlow()

    private val _currentGroup = MutableStateFlow<Group?>(null)
    val currentGroup: StateFlow<Group?> = _currentGroup.asStateFlow()

    private val _groupMembers = MutableStateFlow<List<GroupMember>>(emptyList())
    val groupMembers: StateFlow<List<GroupMember>> = _groupMembers.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _groupPosts = MutableStateFlow<List<com.Shoshin.app.data.db.entities.GroupPostEntity>>(emptyList())
    val groupPosts: StateFlow<List<com.Shoshin.app.data.db.entities.GroupPostEntity>> = _groupPosts.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _limitReached = MutableStateFlow<String?>(null)
    val limitReached: StateFlow<String?> = _limitReached.asStateFlow()

    private val _groupFull = MutableStateFlow<String?>(null)
    val groupFull: StateFlow<String?> = _groupFull.asStateFlow()

    private val _creationSuccess = MutableStateFlow(false)
    val creationSuccess: StateFlow<Boolean> = _creationSuccess.asStateFlow()

    private val _selectedMemberProfile = MutableStateFlow<UserEntity?>(null)
    val selectedMemberProfile: StateFlow<UserEntity?> = _selectedMemberProfile.asStateFlow()

    private val _selectedMemberGroupsInCommon = MutableStateFlow<List<Group>>(emptyList())
    val selectedMemberGroupsInCommon: StateFlow<List<Group>> = _selectedMemberGroupsInCommon.asStateFlow()

    private val _memberProfileLoading = MutableStateFlow(false)
    val memberProfileLoading: StateFlow<Boolean> = _memberProfileLoading.asStateFlow()

    private val _memberActionResult = MutableStateFlow<String?>(null)
    val memberActionResult: StateFlow<String?> = _memberActionResult.asStateFlow()

    fun loadGroups() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getGroups()
            result.onSuccess { _groups.value = it }
            result.onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun createGroup(name: String, description: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _creationSuccess.value = false
            val result = repository.createGroup(name, description)
            result.onSuccess {
                _creationSuccess.value = true
                loadGroups()
            }
            result.onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun resetCreationState() {
        _creationSuccess.value = false
        _error.value = null
    }

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

    fun loadGroupPreviewByCode(inviteCode: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val groupResult = repository.getGroupByInviteCode(inviteCode)
            groupResult.onSuccess { group ->
                _currentGroup.value = group
                val membersResult = repository.getGroupMembers(group.id)
                membersResult.onSuccess { _groupMembers.value = it }
            }
            groupResult.onFailure { _error.value = it.message }
            _isLoading.value = false
        }
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

    private var membersObserverJob: Job? = null

    /** Live leaderboard: keeps [groupMembers] in sync with Firestore as it changes. */
    fun observeGroupMembers(groupId: String) {
        membersObserverJob?.cancel()
        membersObserverJob = viewModelScope.launch {
            repository.getGroupMembersFlow(groupId).collect { _groupMembers.value = it }
        }
    }

    override fun onCleared() {
        super.onCleared()
        membersObserverJob?.cancel()
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

    fun removeMember(groupId: String, targetUserId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.removeMember(groupId, targetUserId)
            result.onSuccess { loadGroupMembers(groupId) }
            result.onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun updateGroupDetails(groupId: String, name: String, description: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.updateGroupDetails(groupId, name, description)
            result.onSuccess { loadGroupMembers(groupId) }
            result.onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun regenerateInviteCode(groupId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.regenerateInviteCode(groupId)
            result.onSuccess { loadGroupMembers(groupId) }
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

    /** Loads a tapped leaderboard member's full profile plus the groups they share with the current user. */
    fun loadMemberProfile(userId: String) {
        viewModelScope.launch {
            _memberProfileLoading.value = true
            _selectedMemberProfile.value = userRepository.getUser(userId)

            val currentUserId = userRepository.userId
            if (currentUserId != null) {
                val myGroups = repository.getGroupsForUser(currentUserId).getOrNull().orEmpty()
                val theirGroups = repository.getGroupsForUser(userId).getOrNull().orEmpty()
                val theirGroupIds = theirGroups.map { it.id }.toSet()
                _selectedMemberGroupsInCommon.value = myGroups.filter { it.id in theirGroupIds }
            }
            _memberProfileLoading.value = false
        }
    }

    fun clearSelectedMember() {
        _selectedMemberProfile.value = null
        _selectedMemberGroupsInCommon.value = emptyList()
    }

    fun clearMemberActionResult() {
        _memberActionResult.value = null
    }

    /** Adds a member directly to the group (no invite/accept flow exists in this app). */
    fun addMemberToCurrentGroup(groupId: String, groupName: String, userId: String, displayName: String) {
        viewModelScope.launch {
            val result = repository.addMemberToGroup(groupId, userId, displayName)
            result.onSuccess {
                _memberActionResult.value = "$displayName added to $groupName"
                AnalyticsManager.logMemberAddedToGroup(groupId, userId)
                loadGroupMembers(groupId)
            }
            result.onFailure {
                val msg = it.message ?: "Couldn't add $displayName"
                _memberActionResult.value = when {
                    msg.startsWith("LIMIT_REACHED:") -> msg.substringAfter(":")
                    msg.startsWith("GROUP_FULL:") -> msg.substringAfter(":")
                    msg == "Already a member of this group" -> "$displayName is already a member"
                    else -> msg
                }
            }
        }
    }
}
