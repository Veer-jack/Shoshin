package com.example.shoshinapp.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.shoshinapp.data.db.AppDatabase
import com.example.shoshinapp.data.db.entities.SocialShareEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

class SocialShareManager(private val context: Context, private val database: AppDatabase) {

    fun shareToPlatform(platform: String, text: String, imagePath: String? = null, userId: String, postId: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = if (imagePath != null) "image/*" else "text/plain"
        
        if (imagePath != null) {
            val file = File(imagePath)
            if (file.exists()) {
                val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                intent.putExtra(Intent.EXTRA_STREAM, uri)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
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
        
        if (packagePrefix != null) {
            intent.`package` = packagePrefix
        }
        
        val chooser = Intent.createChooser(intent, "Share via")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
        
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
