package com.example.shoshinapp

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

class GoogleAuthManager(context: Context, private val auth: FirebaseAuth) {

    private val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(
        context.applicationContext,
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("91496310161-0rc0v6focf6olfnf0djkkehhh4hadv8a.apps.googleusercontent.com")
            .requestEmail()
            .build()
    )

    fun getSignInIntent() = googleSignInClient.signInIntent

    fun handleSignInResult(
        task: Task<GoogleSignInAccount>,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        Log.d("GoogleAuth", "handleSignInResult called")
        try {
            val account = task.getResult(ApiException::class.java)
            if (account != null) {
                Log.d("GoogleAuth", "Got account: ${account.email}")
                authenticateWithFirebase(account, onSuccess, onError)
            } else {
                Log.e("GoogleAuth", "Account is null")
                onError(Exception("Google Account is null"))
            }
        } catch (e: ApiException) {
            Log.e("GoogleAuth", "Sign in failed: status code ${e.statusCode}")
            onError(e)
        } catch (e: Exception) {
            Log.e("GoogleAuth", "Unexpected error during sign in", e)
            onError(e)
        }
    }

    private fun authenticateWithFirebase(
        account: GoogleSignInAccount,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        Log.d("GoogleAuth", "Authenticating with Firebase...")
        val idToken = account.idToken
        if (idToken == null) {
            Log.e("GoogleAuth", "ID Token is null")
            onError(Exception("Google ID Token is null"))
            return
        }
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("GoogleAuth", "Firebase sign in successful!")
                    onSuccess()
                } else {
                    Log.e("GoogleAuth", "Firebase sign in failed: ${task.exception?.message}")
                    onError(task.exception ?: Exception("Firebase sign in failed"))
                }
            }
    }

    fun signOut() = googleSignInClient.signOut()
}
