package com.example.shoshinapp.sync

import android.util.Log
import com.example.shoshinapp.data.AuthRepository
import com.example.shoshinapp.data.db.AppDatabase
import com.example.shoshinapp.data.db.entities.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

sealed class SyncState {
    object Idle : SyncState()
    object Syncing : SyncState()
    data class Success(val message: String) : SyncState()
    data class Error(val message: String) : SyncState()
    data class Conflict(val entityId: String) : SyncState()
}

class SyncManager(
    private val db: AppDatabase,
    private val firestore: FirebaseFirestore,
    private val authRepository: AuthRepository,
    private val conflictResolver: ConflictResolver
) {

    private val _syncState = MutableStateFlow<SyncState>(SyncState.Idle)
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

    private val gson = Gson()
    private val TAG = "SyncManager"

    suspend fun syncAll() {
        try {
            val userId = authRepository.getCurrentUser()?.uid ?: return
            _syncState.value = SyncState.Syncing

            // Sync reflections
            syncReflections(userId)

            // Sync streaks
            syncStreaks(userId)

            // Sync photos
            syncPhotos(userId)

            // Sync Groups
            syncGroups(userId)
            syncGroupPosts(userId)
            syncSocialShares(userId)

            _syncState.value = SyncState.Success("Sync completed successfully")
            Log.d(TAG, "Sync completed for user: $userId")
        } catch (e: Exception) {
            _syncState.value = SyncState.Error(e.message ?: "Sync failed")
            Log.e(TAG, "Sync error", e)
        }
    }

    private suspend fun syncReflections(userId: String) {
        val pendingReflections = db.reflectionDao().getPendingReflections(userId)

        for (local in pendingReflections) {
            try {
                val remoteDoc = firestore
                    .collection("reflections")
                    .document(userId)
                    .collection("entries")
                    .document(local.reflectionId)
                    .get()
                    .await()

                var entityToPush = local

                if (remoteDoc.exists()) {
                    val remote = remoteDoc.toObject(ReflectionEntity::class.java) ?: continue

                    // Check for conflict
                    if (local.version < remote.version && local.content != remote.content) {
                        entityToPush = handleReflectionConflict(userId, local, remote) ?: continue
                    }
                }

                // Push to Firebase
                firestore
                    .collection("reflections")
                    .document(userId)
                    .collection("entries")
                    .document(entityToPush.reflectionId)
                    .set(entityToPush)
                    .await()

                // Mark as synced
                db.reflectionDao().updateReflection(
                    entityToPush.copy(syncStatus = "synced")
                )
                Log.d(TAG, "Synced reflection: ${entityToPush.reflectionId}")
            } catch (e: Exception) {
                Log.e(TAG, "Error syncing reflection ${local.reflectionId}", e)
            }
        }

        // Pull fresh data from Firebase
        pullRemoteReflections(userId)
    }

    private suspend fun syncStreaks(userId: String) {
        val pendingStreaks = db.streakDao().getPendingStreaks(userId)

        for (streak in pendingStreaks) {
            try {
                firestore
                    .collection("streaks")
                    .document(userId)
                    .collection("entries")
                    .document(streak.streakId)
                    .set(streak)
                    .await()

                db.streakDao().updateStreak(
                    streak.copy(syncStatus = "synced")
                )
                Log.d(TAG, "Synced streak: ${streak.streakId}")
            } catch (e: Exception) {
                Log.e(TAG, "Error syncing streak ${streak.streakId}", e)
            }
        }

        // Pull fresh data
        pullRemoteStreaks(userId)
    }

    private suspend fun syncPhotos(userId: String) {
        val pendingPhotos = db.photoDao().getPendingPhotos(userId)

        for (photo in pendingPhotos) {
            try {
                firestore
                    .collection("photos")
                    .document(userId)
                    .collection("entries")
                    .document(photo.photoId)
                    .set(photo)
                    .await()

                db.photoDao().updatePhoto(
                    photo.copy(syncStatus = "synced")
                )
                Log.d(TAG, "Synced photo: ${photo.photoId}")
            } catch (e: Exception) {
                Log.e(TAG, "Error syncing photo ${photo.photoId}", e)
            }
        }

        // Pull fresh data
        pullRemotePhotos(userId)
    }

    private suspend fun syncGroups(userId: String) {
        val pendingGroups = db.groupDao().getPendingGroups()
        for (group in pendingGroups) {
            try {
                firestore.collection("groups").document(group.groupId).set(group).await()
                db.groupDao().updateGroup(group.copy(syncStatus = "synced"))
            } catch (e: Exception) {
                Log.e(TAG, "Error syncing group ${group.groupId}", e)
            }
        }
    }

    private suspend fun syncGroupPosts(userId: String) {
        val pendingPosts = db.groupPostDao().getPendingPosts()
        for (post in pendingPosts) {
            try {
                firestore.collection("groups").document(post.groupId)
                    .collection("posts").document(post.postId).set(post).await()
                db.groupPostDao().updatePost(post.copy(syncStatus = "synced"))
            } catch (e: Exception) {
                Log.e(TAG, "Error syncing post ${post.postId}", e)
            }
        }
    }

    private suspend fun syncSocialShares(userId: String) {
        val pendingShares = db.socialShareDao().getPendingShares()
        for (share in pendingShares) {
            try {
                firestore.collection("shares").document(share.shareId).set(share).await()
                db.socialShareDao().updateShare(share.copy(syncStatus = "synced"))
            } catch (e: Exception) {
                Log.e(TAG, "Error syncing share ${share.shareId}", e)
            }
        }
    }

    private suspend fun pullRemoteReflections(userId: String) {
        try {
            val docs = firestore
                .collection("reflections")
                .document(userId)
                .collection("entries")
                .get()
                .await()

            for (doc in docs) {
                val remote = doc.toObject(ReflectionEntity::class.java) ?: continue
                val local = db.reflectionDao().getReflection(remote.reflectionId)

                if (local == null) {
                    db.reflectionDao().insertReflection(
                        remote.copy(syncStatus = "synced")
                    )
                } else if (remote.version > local.version) {
                    db.reflectionDao().updateReflection(
                        remote.copy(syncStatus = "synced")
                    )
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error pulling reflections", e)
        }
    }

    private suspend fun pullRemoteStreaks(userId: String) {
        try {
            val docs = firestore
                .collection("streaks")
                .document(userId)
                .collection("entries")
                .get()
                .await()

            for (doc in docs) {
                val remote = doc.toObject(StreakEntity::class.java) ?: continue
                val local = db.streakDao().getStreak(remote.streakId)
                if (local == null) {
                    db.streakDao().insertStreak(remote.copy(syncStatus = "synced"))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error pulling streaks", e)
        }
    }

    private suspend fun pullRemotePhotos(userId: String) {
        try {
            val docs = firestore
                .collection("photos")
                .document(userId)
                .collection("entries")
                .get()
                .await()

            for (doc in docs) {
                val remote = doc.toObject(PhotoEntity::class.java) ?: continue
                val local = db.photoDao().getPhoto(remote.photoId)
                if (local == null) {
                    db.photoDao().insertPhoto(remote.copy(syncStatus = "synced"))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error pulling photos", e)
        }
    }

    private suspend fun handleReflectionConflict(
        userId: String,
        local: ReflectionEntity,
        remote: ReflectionEntity
    ): ReflectionEntity? = suspendCancellableCoroutine { continuation ->
        _syncState.value = SyncState.Conflict(local.reflectionId)

        conflictResolver.resolveReflectionConflict(local, remote) { resolution ->
            val resolved = when (resolution) {
                is ConflictResolution.UseLocal -> local.copy(version = remote.version + 1)
                is ConflictResolution.UseRemote -> remote
                is ConflictResolution.Merge -> resolution.merged
            }
            continuation.resume(resolved)
        }
    }
}
