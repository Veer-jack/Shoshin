package com.Shoshin.app.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Shoshin.app.data.db.entities.UserEntity
import com.Shoshin.app.data.user.UserRepository
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

    fun updateProfile(name: String, phone: String, email: String, bio: String = "") {
        val currentUser = _user.value ?: return
        viewModelScope.launch {
            _isLoading.value = true
            val updatedUser = currentUser.copy(
                displayName = name,
                phone = phone,
                email = email.ifBlank { null },
                bio = bio.ifBlank { null },
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
                    // Every picture goes to the same storage path, so a re-upload can come
                    // back with a byte-identical URL — Coil would then serve the previous
                    // image from cache on every screen. A version param keeps the URL unique.
                    val separator = if (url.contains('?')) "&" else "?"
                    val versionedUrl = "$url${separator}v=${System.currentTimeMillis()}"
                    val updatedUser = currentUser.copy(
                        profilePictureUrl = versionedUrl,
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
