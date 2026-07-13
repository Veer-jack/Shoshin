package com.example.shoshinapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoshinapp.data.db.entities.UserEntity
import com.example.shoshinapp.data.user.UserRepository
import com.example.shoshinapp.data.ShoshinRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val userRepository: UserRepository,
    private val shoshinRepository: ShoshinRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _user = MutableStateFlow<UserEntity?>(null)
    val user: StateFlow<UserEntity?> = _user.asStateFlow()

    init {
        loadUser()
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
        viewModelScope.launch {
            // In a real app, delete from Firestore and Storage first
            val user = auth.currentUser
            user?.delete()?.addOnCompleteListener {
                viewModelScope.launch {
                    shoshinRepository.logout()
                    onComplete()
                }
            }
        }
    }
}
