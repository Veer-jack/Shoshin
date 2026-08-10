package com.Shoshin.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Shoshin.app.data.db.entities.UserEntity
import com.Shoshin.app.data.user.UserRepository
import com.Shoshin.app.data.ShoshinRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SettingsViewModel(
    private val userRepository: UserRepository,
    private val shoshinRepository: ShoshinRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _user = MutableStateFlow<UserEntity?>(null)
    val user: StateFlow<UserEntity?> = _user.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadUser()
    }

    fun clearError() {
        _error.value = null
    }

    private fun loadUser() {
        val uid = userRepository.userId ?: return
        viewModelScope.launch {
            userRepository.getUserFlow(uid).collect {
                _user.value = it
            }
        }
    }

    fun updateNotifications(enabled: Boolean) {
        val currentUser = _user.value ?: return
        viewModelScope.launch {
            userRepository.updateUser(currentUser.copy(notificationsEnabled = enabled))
        }
    }

    fun updateNotificationTime(hour: Int, minute: Int) {
        val currentUser = _user.value ?: return
        val timeString = String.format("%02d:%02d", hour, minute)
        viewModelScope.launch {
            userRepository.updateUser(currentUser.copy(notificationTime = timeString))
            shoshinRepository.saveAlarm(hour, minute) // Keep legacy alarm in sync if needed
        }
    }

    fun updateProductiveHours(start: String, end: String) {
        val currentUser = _user.value ?: return
        viewModelScope.launch {
            userRepository.updateUser(currentUser.copy(
                productiveStartTime = start,
                productiveEndTime = end
            ))
        }
    }

    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            shoshinRepository.logout()
            auth.signOut()
            onComplete()
        }
    }

    fun deleteAccount(onComplete: () -> Unit) {
        val firebaseUser = auth.currentUser ?: return
        val uid = firebaseUser.uid
        viewModelScope.launch {
            // Wipe app data first — if auth deletion fails below, the account is orphaned
            // rather than the data being stranded under a still-valid account.
            userRepository.deleteAccountData(uid)
            try {
                firebaseUser.delete().await()
                shoshinRepository.logout()
                onComplete()
            } catch (e: com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException) {
                _error.value = "Please log out and back in, then try again."
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Failed to delete account."
            }
        }
    }
}
