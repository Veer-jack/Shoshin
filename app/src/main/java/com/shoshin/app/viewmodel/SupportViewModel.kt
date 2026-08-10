package com.Shoshin.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Shoshin.app.BuildConfig
import com.Shoshin.app.data.FeedbackRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val MAX_FEEDBACK_LENGTH = 500

class SupportViewModel(
    private val repository: FeedbackRepository
) : ViewModel() {

    private val _feedbackText = MutableStateFlow("")
    val feedbackText: StateFlow<String> = _feedbackText.asStateFlow()

    private val _isSending = MutableStateFlow(false)
    val isSending: StateFlow<Boolean> = _isSending.asStateFlow()

    private val _sentConfirmation = MutableStateFlow(false)
    val sentConfirmation: StateFlow<Boolean> = _sentConfirmation.asStateFlow()

    val appVersion: String = BuildConfig.VERSION_NAME

    fun updateFeedbackText(text: String) {
        if (text.length <= MAX_FEEDBACK_LENGTH) {
            _feedbackText.value = text
        }
    }

    fun sendFeedback() {
        val text = _feedbackText.value.trim()
        if (text.isEmpty()) return
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        viewModelScope.launch {
            _isSending.value = true
            repository.submitFeedback(userId, text, appVersion)
            _feedbackText.value = ""
            _sentConfirmation.value = true
            _isSending.value = false
        }
    }

    fun clearConfirmation() {
        _sentConfirmation.value = false
    }
}
