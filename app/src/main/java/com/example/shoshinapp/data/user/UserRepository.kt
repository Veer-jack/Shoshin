package com.example.shoshinapp.data.user

import android.graphics.Bitmap
import com.example.shoshinapp.data.db.dao.UserDao
import com.example.shoshinapp.data.db.entities.UserEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream

class UserRepository(
    private val userDao: UserDao,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val auth: FirebaseAuth
) {
    val userId: String? get() = auth.currentUser?.uid

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
        userDao.insertUser(user) // Use insert with REPLACE strategy to handle both new and existing
        try {
            firestore.collection("users").document(user.userId).set(user).await()
        } catch (e: Exception) {
            // Handle error (queue for sync)
        }
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
