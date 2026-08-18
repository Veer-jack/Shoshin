package com.Shoshin.app.utils

import android.content.Context
import android.widget.Toast
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException

object ErrorHandler {

    fun mapFirebaseError(e: Exception): String {
        val message = e.message ?: ""
        return when {
            e is FirebaseNetworkException -> "Network error. Please check your internet connection."
            e is FirebaseAuthInvalidUserException -> "User account not found or disabled."
            e is FirebaseAuthInvalidCredentialsException -> "Invalid credentials. Please check your input."
            e is FirebaseAuthUserCollisionException -> "An account with this email already exists."
            message.contains("TOO_MANY_ATTEMPTS_EXCEEDED") -> "Too many attempts. Please try again later."
            message.contains("SMS_QUOTA_EXCEEDED") -> "SMS quota exceeded. Please contact support or try later."
            message.contains("app identifier") -> "Security verification failed (App Check). Please ensure you're using an official version."
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
