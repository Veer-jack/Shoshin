package com.example.shoshinapp

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class EmailAuthManager(private val auth: FirebaseAuth) {

    private val TAG = "EmailAuth"

    fun sendVerificationEmail(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        Log.d(TAG, "sendVerificationEmail called for: $email")
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "User created successfully. Sending link...")
                    sendLink(task.result?.user, onSuccess, onError)
                } else {
                    if (task.exception is FirebaseAuthUserCollisionException) {
                        Log.d(TAG, "User already exists. Attempting sign-in...")
                        resendToExistingUser(email, password, onSuccess, onError)
                    } else {
                        Log.e(TAG, "Create user failed: ${task.exception?.message}")
                        onError(task.exception ?: Exception("Failed to initiate email auth"))
                    }
                }
            }
    }

    private fun resendToExistingUser(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Sign-in successful. Sending link...")
                    sendLink(task.result?.user, onSuccess, onError)
                } else {
                    Log.e(TAG, "Sign-in failed for existing user: ${task.exception?.message}")
                    onError(task.exception ?: Exception("Failed to sign in existing user. Check your password."))
                }
            }
    }

    private fun sendLink(
        user: com.google.firebase.auth.FirebaseUser?,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        if (user == null) {
            Log.e(TAG, "sendLink aborted: user is null")
            onError(Exception("User not found"))
            return
        }

        user.sendEmailVerification()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Verification link sent SUCCESS")
                    onSuccess()
                } else {
                    Log.e(TAG, "Verification link send FAILED: ${task.exception?.message}")
                    onError(task.exception ?: Exception("Failed to send verification email"))
                }
            }
    }

    fun verifyEmail(
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val user = auth.currentUser
        Log.d(TAG, "verifyEmail called. User logged in: ${user != null}")
        if (user == null) {
            onError(Exception("Session expired. Please try again."))
            return
        }

        user.reload().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "User reload SUCCESS. isEmailVerified: ${user.isEmailVerified}")
                if (user.isEmailVerified) {
                    onSuccess()
                } else {
                    onError(Exception("Email not verified yet. Please check your inbox (and spam)."))
                }
            } else {
                Log.e(TAG, "User reload FAILED: ${task.exception?.message}")
                onError(task.exception ?: Exception("Verification check failed"))
            }
        }
    }
}
