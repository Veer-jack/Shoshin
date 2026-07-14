package com.example.shoshinapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoshinapp.data.ContactsRepository
import com.example.shoshinapp.data.models.UserSummary
import com.example.shoshinapp.data.user.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InviteViewModel(
    private val userRepository: UserRepository,
    private val contactsRepository: ContactsRepository
) : ViewModel() {

    private val firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance()

    private val _suggestedFriends = MutableStateFlow<List<UserSummary>>(emptyList())
    val suggestedFriends: StateFlow<List<UserSummary>> = _suggestedFriends.asStateFlow()

    private val _searchResults = MutableStateFlow<List<UserSummary>>(emptyList())
    val searchResults: StateFlow<List<UserSummary>> = _searchResults.asStateFlow()

    fun loadAndMatchContacts() {
        viewModelScope.launch {
            try {
                val rawContacts = contactsRepository.fetchContacts()
                val (onShoshin, notOnShoshin) = contactsRepository
                    .matchContactsWithShoshinUsers(rawContacts, firestore)

                // Show Shoshin users first then contacts
                _suggestedFriends.value = onShoshin + notOnShoshin
            } catch (e: Exception) {
                // Handle permission or other errors
            }
        }
    }

    fun loadContacts() {
        loadAndMatchContacts()
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
        return userRepository.userId?.take(8)?.uppercase() ?: ""
    }

    fun getInviteLink(): String {
        return "https://shoshin.app/join/${getUserInviteCode()}"
    }
}
