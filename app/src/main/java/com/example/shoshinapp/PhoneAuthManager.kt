package com.example.shoshinapp

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthOptions
import java.util.concurrent.TimeUnit

class PhoneAuthManager(private val auth: FirebaseAuth) {

    var verificationId: String? = null
    var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    private val TAG = "PhoneAuth"

    fun startPhoneAuth(
        phone: String,
        activity: android.app.Activity,
        onCodeSent: (verificationId: String) -> Unit,
        onError: (Exception) -> Unit
    ) {
        Log.d(TAG, "startPhoneAuth called for: $phone")

        val cleanPhone = phone.replace(Regex("[^0-9+]"), "")
        val formattedPhone = when {
            cleanPhone.startsWith("+") -> cleanPhone
            cleanPhone.length == 10 -> "+91$cleanPhone"
            else -> {
                Log.e(TAG, "Unrecognized phone format: $cleanPhone")
                onError(Exception("Please enter a valid phone number with country code"))
                return
            }
        }
        
        Log.d(TAG, "Final formatted phone: $formattedPhone")

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: com.google.firebase.auth.PhoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted: Auto-verification successful")
            }

            override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
                Log.d(TAG, "onCodeSent success: Verification ID = $id")
                verificationId = id
                resendToken = token
                onCodeSent(id)
            }

            override fun onVerificationFailed(e: com.google.firebase.FirebaseException) {
                Log.e(TAG, "onVerificationFailed: ${e.message}", e)
                onError(e)
            }
        }

        try {
            Log.d(TAG, "Initiating PhoneAuthProvider.verifyPhoneNumber...")
            
            // Force reCAPTCHA flow for testing
            auth.firebaseAuthSettings.forceRecaptchaFlowForTesting(true)

            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(formattedPhone)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(callbacks)
                .build()
            
            PhoneAuthProvider.verifyPhoneNumber(options)
        } catch (e: Exception) {
            Log.e(TAG, "Error in verifyPhoneNumber setup", e)
            onError(e)
        }
    }

    fun resendOTP(
        phone: String,
        activity: android.app.Activity,
        onCodeSent: (verificationId: String) -> Unit,
        onError: (Exception) -> Unit
    ) {
        Log.d(TAG, "resendOTP called. Has token: ${resendToken != null}")
        if (resendToken == null) {
            startPhoneAuth(phone, activity, onCodeSent, onError)
            return
        }

        val cleanPhone = phone.replace(Regex("[^0-9+]"), "")
        val formattedPhone = if (cleanPhone.startsWith("+")) cleanPhone else "+91$cleanPhone"

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: com.google.firebase.auth.PhoneAuthCredential) {}
            override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
                Log.d(TAG, "onCodeSent (resend) success: Verification ID = $id")
                verificationId = id
                resendToken = token
                onCodeSent(id)
            }
            override fun onVerificationFailed(e: com.google.firebase.FirebaseException) {
                Log.e(TAG, "onVerificationFailed (resend): ${e.message}", e)
                onError(e)
            }
        }

        try {
            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(formattedPhone)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(callbacks)
                .setForceResendingToken(resendToken!!)
                .build()
            
            PhoneAuthProvider.verifyPhoneNumber(options)
        } catch (e: Exception) {
            Log.e(TAG, "Error in verifyPhoneNumber (resend) setup", e)
            onError(e)
        }
    }

    fun verifyOTP(
        otp: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        Log.d(TAG, "verifyOTP called with code: $otp for ID: $verificationId")
        if (verificationId == null) {
            Log.e(TAG, "Cannot verify: verificationId is null")
            onError(Exception("Verification ID not found"))
            return
        }

        val credential = PhoneAuthProvider.getCredential(verificationId!!, otp)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Firebase signInWithCredential SUCCESS")
                    onSuccess()
                } else {
                    Log.e(TAG, "Firebase signInWithCredential FAILED: ${task.exception?.message}")
                    onError(task.exception ?: Exception("Verification failed"))
                }
            }
    }
}
