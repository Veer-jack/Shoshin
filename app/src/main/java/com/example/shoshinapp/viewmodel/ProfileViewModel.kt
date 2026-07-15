package com.example.shoshinapp.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoshinapp.data.db.entities.UserEntity
import com.example.shoshinapp.data.user.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: UserRepository) : ViewModel() {

    private val _user = MutableStateFlow<UserEntity?>(null)
    val user: StateFlow<UserEntity?> = _user.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadUser()
    }

    fun loadUser() {
        val uid = repository.userId ?: return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Try to get user from local or remote
                val userEntity = repository.getUser(uid)
                _user.value = userEntity
                
                // Subscribe to real-time updates
                repository.getUserFlow(uid).collect {
                    _user.value = it
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }

    fun updateProfile(name: String, bio: String?) {
        val currentUser = _user.value ?: return
        viewModelScope.launch {
            _isLoading.value = true
            val updatedUser = currentUser.copy(
                displayName = name,
                bio = bio,
                lastUpdated = System.currentTimeMillis()
            )
            repository.updateUser(updatedUser)
            _isLoading.value = false
        }
    }

    fun uploadPicture(bitmap: Bitmap) {
        viewModelScope.launch {
            _isLoading.value = true
            android.util.Log.d("Profile", "Starting image upload...")
            val result = repository.uploadProfilePicture(bitmap)
            result.onSuccess { url ->
                android.util.Log.d("Profile", "Upload success: $url")
                val currentUser = _user.value
                if (currentUser != null) {
                    val updatedUser = currentUser.copy(
                        profilePictureUrl = url,
                        lastUpdated = System.currentTimeMillis()
                    )
                    repository.updateUser(updatedUser)
                }
            }
            result.onFailure {
                android.util.Log.e("Profile", "Upload failed", it)
                _error.value = "Upload failed: ${it.localizedMessage}"
            }
            _isLoading.value = false
        }
    }
    
    fun clearError() {
        _error.value = null
    }
}
