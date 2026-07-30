package com.Shoshin.app

import android.app.Activity
import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import android.util.Log
import kotlinx.coroutines.tasks.await

class GoogleAuthManager(context: Context, private val auth: FirebaseAuth) {

    private val TAG = "GoogleAuthManager"
    
    private val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(
        context,
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("91496310161-0rc0v6focf6olfnf0djkkehhh4hadv8a.apps.googleusercontent.com")
            .requestEmail()
            .requestProfile()
            .build()
    )

    fun getSignInIntent() = googleSignInClient.signInIntent

    fun handleSignInResult(
        task: Task<GoogleSignInAccount>,
        onSuccess: (String) -> Unit,  // Pass userId
        onError: (String) -> Unit     // Pass error message
    ) {
        Log.d(TAG, "handleSignInResult called")
        try {
            val account = task.getResult(ApiException::class.java)
            
            if (account == null) {
                val error = "Google Account is null"
                Log.e(TAG, error)
                onError(error)
                return
            }

            Log.d(TAG, "Google Sign-In successful. Email: ${account.email}, ID Token: ${account.idToken != null}")
            
            // Authenticate with Firebase
            authenticateWithFirebase(account, onSuccess, onError)
            
        } catch (e: ApiException) {
            val error = "Google Sign-In failed with status code: ${e.statusCode}. Error: ${e.message}"
            Log.e(TAG, error)
            onError(error)
        } catch (e: Exception) {
            val error = "Unexpected error during Google Sign-In: ${e.message}"
            Log.e(TAG, error, e)
            onError(error)
        }
    }

    private fun authenticateWithFirebase(
        account: GoogleSignInAccount,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        Log.d(TAG, "Starting Firebase authentication with Google account...")
        
        val idToken = account.idToken
        if (idToken.isNullOrEmpty()) {
            val error = "Google ID Token is null or empty"
            Log.e(TAG, error)
            onError(error)
            return
        }

        Log.d(TAG, "ID Token obtained. Creating Firebase credential...")
        
        try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            
            Log.d(TAG, "Signing in with Firebase credential...")
            auth.signInWithCredential(credential)
                .addOnSuccessListener { authResult ->
                    val userId = authResult.user?.uid ?: ""
                    Log.d(TAG, "Firebase authentication successful! UserID: $userId")
                    onSuccess(userId)
                }
                .addOnFailureListener { exception ->
                    val error = "Firebase sign-in failed: ${exception.message}"
                    Log.e(TAG, error, exception)
                    onError(error)
                }
                .addOnCanceledListener {
                    val error = "Firebase sign-in was cancelled"
                    Log.e(TAG, error)
                    onError(error)
                }
        } catch (e: Exception) {
            val error = "Error creating Firebase credential: ${e.message}"
            Log.e(TAG, error, e)
            onError(error)
        }
    }

    fun signOut() {
        Log.d(TAG, "Signing out from Google...")
        googleSignInClient.signOut()
        auth.signOut()
        Log.d(TAG, "Signed out successfully")
    }
}
