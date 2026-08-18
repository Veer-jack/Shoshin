package com.Shoshin.app.data.user

import android.graphics.Bitmap
import com.Shoshin.app.data.db.dao.UserDao
import com.Shoshin.app.data.db.entities.UserEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream

class UserRepository(
    private val userDao: UserDao,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val auth: FirebaseAuth
) {
    val userId: String? get() = auth.currentUser?.uid

    /**
     * Emits the signed-in uid, and re-emits on every sign-in/sign-out. ViewModels built before
     * authentication finishes — the nav graph constructs several while the Auth screen is still
     * up — otherwise read `userId` once, get null, and never recover. The listener fires
     * immediately on registration, so a collector always gets the current state first.
     */
    val userIdFlow: Flow<String?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { trySend(it.currentUser?.uid) }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    fun getUserFlow(uid: String): Flow<UserEntity?> = userDao.getUserFlow(uid)

    suspend fun getUser(uid: String): UserEntity? {
        // Try local first
        var user = userDao.getUser(uid)
        if (user == null) {
            // Fetch from Firestore
            try {
                val doc = firestore.collection("users").document(uid).get().await()
                user = doc.toObject(UserEntity::class.java)
                if (user != null) {
                    userDao.insertUser(user)
                } else {
                    // Create minimal profile if none exists but user is authenticated
                    val firebaseUser = auth.currentUser
                    if (firebaseUser != null && firebaseUser.uid == uid) {
                        val newUser = UserEntity(
                            userId = uid,
                            displayName = firebaseUser.displayName ?: "New User",
                            email = firebaseUser.email,
                            phone = firebaseUser.phoneNumber,
                            photoUrl = firebaseUser.photoUrl?.toString()
                        )
                        updateUser(newUser)
                        user = newUser
                    }
                }
            } catch (e: Exception) {
                // Log error
            }
        }
        return user
    }

    suspend fun updateUser(user: UserEntity) {
        // 1. Always save to local DB first (Non-blocking source of truth)
        userDao.insertUser(user) 
        
        // 2. Sync to cloud in the background so slow network doesn't block the app
        kotlinx.coroutines.GlobalScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                firestore.collection("users").document(user.userId).set(user).await()
                android.util.Log.d("UserRepository", "Cloud sync successful for user: ${user.userId}")
            } catch (e: Exception) {
                android.util.Log.e("UserRepository", "Cloud sync failed: ${e.message}. Will retry via SyncWorker.")
            }
        }
    }

    /** Removes this user's Firestore document and local Room row. Does not touch Firebase Auth. */
    suspend fun deleteAccountData(uid: String) {
        try {
            firestore.collection("users").document(uid).delete().await()
        } catch (e: Exception) {
            android.util.Log.e("UserRepository", "Failed to delete Firestore user doc: ${e.message}")
        }
        userDao.getUser(uid)?.let { userDao.deleteUser(it) }
    }

    suspend fun uploadProfilePicture(bitmap: Bitmap): Result<String> {
        val uid = userId ?: return Result.failure(Exception("Not authenticated"))
        return try {
            val ref = storage.reference.child("profiles/$uid/picture.jpg")
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos)
            val data = baos.toByteArray()
            
            ref.putBytes(data).await()
            val url = ref.downloadUrl.await().toString()
            Result.success(url)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
