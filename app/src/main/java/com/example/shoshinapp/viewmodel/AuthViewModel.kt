package com.example.shoshinapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoshinapp.data.AuthRepository
import com.example.shoshinapp.GoogleAuthManager
import com.example.shoshinapp.PhoneAuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val googleAuthManager: GoogleAuthManager,
    private val phoneAuthManager: PhoneAuthManager
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private val _verificationId = MutableStateFlow<String?>(null)
    val verificationId: StateFlow<String?> = _verificationId

    fun startPhoneAuth(phone: String, activity: android.app.Activity) {
        _authState.value = AuthState.Loading
        phoneAuthManager.startPhoneAuth(
            phone,
            activity,
            onCodeSent = { id ->
                _verificationId.value = id
                _authState.value = AuthState.CodeSent
            },
            onError = { e ->
                _authState.value = AuthState.Error(e.message ?: "Error")
            }
        )
    }

    fun verifyPhoneCode(code: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val verificationId = _verificationId.value ?: return@launch
            val result = authRepository.signInWithPhone(verificationId, code)
            
            _authState.value = if (result.isSuccess) {
                AuthState.Success(result.getOrNull() ?: "")
            } else {
                AuthState.Error(result.exceptionOrNull()?.message ?: "Error")
            }
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.signInWithGoogle(idToken)
            
            _authState.value = if (result.isSuccess) {
                AuthState.Success(result.getOrNull() ?: "")
            } else {
                AuthState.Error(result.exceptionOrNull()?.message ?: "Error")
            }
        }
    }

    fun logout() {
        authRepository.logout()
        googleAuthManager.signOut()
        _authState.value = AuthState.Idle
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object CodeSent : AuthState()
    data class Success(val userId: String) : AuthState()
    data class Error(val message: String) : AuthState()
}
