package com.example.shoshinapp.utils

import android.content.Context
import android.widget.Toast
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException

object ErrorHandler {

    fun mapFirebaseError(e: Exception): String {
        return when (e) {
            is FirebaseNetworkException -> "Network error. Please check your internet connection."
            is FirebaseAuthInvalidUserException -> "User account not found or disabled."
            is FirebaseAuthInvalidCredentialsException -> "Invalid credentials. Please check your input."
            is FirebaseAuthUserCollisionException -> "An account with this email already exists."
            else -> e.localizedMessage ?: "An unexpected error occurred. Please try again."
        }
    }

    fun showErrorToast(context: Context, e: Exception) {
        val message = mapFirebaseError(e)
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    fun showMessageToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
