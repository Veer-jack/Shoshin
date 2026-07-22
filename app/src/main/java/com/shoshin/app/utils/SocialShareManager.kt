package com.shoshin.app.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.shoshin.app.data.db.AppDatabase
import com.shoshin.app.data.db.entities.SocialShareEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

class SocialShareManager(context: Context, private val database: AppDatabase) {

    private val appContext = context.applicationContext

    fun shareToPlatform(platform: String, text: String, imagePath: String? = null, userId: String, postId: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = if (imagePath != null) "image/*" else "text/plain"
        
        if (imagePath != null) {
            val file = File(imagePath)
            if (file.exists()) {
                try {
                    val uri = FileProvider.getUriForFile(appContext, "${appContext.packageName}.fileprovider", file)
                    intent.putExtra(Intent.EXTRA_STREAM, uri)
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    // Grant permission via ClipData as well (best practice for Android 10+)
                    intent.clipData = android.content.ClipData.newRawUri("", uri)
                } catch (e: Exception) {
                    android.util.Log.e("SocialShareManager", "Error getting URI for file: ${e.message}")
                }
            }
        }
        
        intent.putExtra(Intent.EXTRA_TEXT, text)
        
        val packagePrefix = when (platform.lowercase()) {
            "instagram" -> "com.instagram.android"
            "whatsapp" -> "com.whatsapp"
            "snapchat" -> "com.snapchat.android"
            "facebook" -> "com.facebook.katana"
            "twitter" -> "com.twitter.android"
            else -> null
        }
        
        val chooser = if (packagePrefix != null) {
            // Check if the package is installed and can handle the intent
            val packageManager = appContext.packageManager
            val activities = packageManager.queryIntentActivities(intent, android.content.pm.PackageManager.MATCH_DEFAULT_ONLY)
            val isHandlerInstalled = activities.any { it.activityInfo.packageName == packagePrefix }
            
            if (isHandlerInstalled) {
                intent.`package` = packagePrefix
                // For specific package, we might not need a chooser, but it's safer for UI consistency
                Intent.createChooser(intent, "Share via $platform")
            } else {
                Intent.createChooser(intent, "Share via")
            }
        } else {
            Intent.createChooser(intent, "Share via")
        }
        
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        // Also grant URI permission to the chooser itself to propagate it
        chooser.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        try {
            appContext.startActivity(chooser)
        } catch (e: Exception) {
            android.util.Log.e("SocialShareManager", "Failed to start share activity: ${e.message}")
        }
        
        recordShare(platform, postId, userId)
    }

    private fun recordShare(platform: String, postId: String, userId: String) {
        val share = SocialShareEntity(
            shareId = UUID.randomUUID().toString(),
            postId = postId,
            userId = userId,
            platform = platform,
            shareUrl = null,
            isSuccessful = true,
            sharedAt = System.currentTimeMillis(),
            syncStatus = "pending"
        )
        
        CoroutineScope(Dispatchers.IO).launch {
            database.socialShareDao().insertShare(share)
        }
    }
}
