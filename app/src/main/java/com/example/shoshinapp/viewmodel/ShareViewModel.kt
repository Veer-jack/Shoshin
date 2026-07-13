package com.example.shoshinapp.viewmodel

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoshinapp.utils.ShareCardGenerator
import com.example.shoshinapp.utils.AnalyticsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ShareViewModel(private val context: Context) : ViewModel() {

    private val cardGenerator = ShareCardGenerator(context)

    private val _customDescription = MutableStateFlow("")
    val customDescription: StateFlow<String> = _customDescription

    private val _shareBitmap = MutableStateFlow<Bitmap?>(null)
    val shareBitmap: StateFlow<Bitmap?> = _shareBitmap

    fun updateDescription(text: String) {
        if (text.length <= 250) {
            _customDescription.value = text
        }
    }

    fun generatePreview(streak: Int, habitName: String, startDate: Long) {
        viewModelScope.launch {
            val dateStr = if (startDate > 0) SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(Date(startDate)) else "January 1, 2024"
            val bitmap = cardGenerator.generateStreakCard(
                streak = streak,
                habitName = habitName,
                description = _customDescription.value,
                startDate = dateStr,
                isMilestone = isMilestone(streak)
            )
            _shareBitmap.value = bitmap
        }
    }

    private fun isMilestone(streak: Int) = streak in listOf(7, 30, 100, 365)

    fun shareToPlatform(platform: String, streak: Int) {
        val bitmap = _shareBitmap.value ?: return
        val file = cardGenerator.saveBitmapToFile(bitmap, "shoshin_share_${System.currentTimeMillis()}") ?: return
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

        val caption = getCaption(platform, streak)
        
        val intent = when (platform) {
            "Instagram" -> createShareIntent("com.instagram.android", uri, caption)
            "Twitter" -> createShareIntent("com.twitter.android", uri, caption)
            "Facebook" -> createShareIntent("com.facebook.katana", uri, caption)
            "WhatsApp" -> createShareIntent("com.whatsapp", uri, caption)
            else -> createGenericShareIntent(uri, caption)
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(intent)
            trackShareEvent(platform, streak)
        } catch (e: Exception) {
            // Fallback to generic share if app not found
            context.startActivity(Intent.createChooser(createGenericShareIntent(uri, caption), "Share with").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }

    private fun createShareIntent(packageName: String, uri: Uri, caption: String): Intent {
        return Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, caption)
            setPackage(packageName)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    private fun createGenericShareIntent(uri: Uri, caption: String): Intent {
        return Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, caption)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    private fun getCaption(platform: String, streak: Int): String {
        return when (platform) {
            "Twitter" -> "Day $streak of my morning routine! 🔥 Consistency is key. #ShoshinApp #MorningHabits"
            else -> "Day $streak of my morning routine! 🔥 I'm building consistency with Shoshin App. #MorningHabits"
        }
    }

    fun saveToGallery() {
        val bitmap = _shareBitmap.value ?: return
        viewModelScope.launch {
            val filename = "Shoshin_Streak_${System.currentTimeMillis()}.png"
            val resolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/Shoshin")
                }
            }

            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            uri?.let {
                resolver.openOutputStream(it).use { out ->
                    if (out != null) {
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                    }
                }
                Log.d("Share", "Saved to gallery: $uri")
            }
        }
    }

    private fun trackShareEvent(platform: String, streak: Int) {
        AnalyticsManager.logShareCompleted(platform, streak, "streak")
        Log.d("Analytics", "share_to_${platform.lowercase()}: streak=$streak")
    }
}
