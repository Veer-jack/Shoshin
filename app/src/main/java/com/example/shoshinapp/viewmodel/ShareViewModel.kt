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

    private val _selectedStyle = MutableStateFlow("streak")
    val selectedStyle: StateFlow<String> = _selectedStyle

    private val _customDescription = MutableStateFlow("")
    val customDescription: StateFlow<String> = _customDescription

    private val _shareBitmap = MutableStateFlow<Bitmap?>(null)
    val shareBitmap: StateFlow<Bitmap?> = _shareBitmap

    fun setStyle(style: String) {
        _selectedStyle.value = style
    }

    fun updateDescription(text: String) {
        if (text.length <= 250) {
            _customDescription.value = text
        }
    }

    fun generatePreview(streak: Int, habitName: String, startDate: Long) {
        viewModelScope.launch {
            val dateStr = if (startDate > 0) SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(Date(startDate)) else "January 1, 2024"
            val bitmap = when (_selectedStyle.value) {
                "streak" -> cardGenerator.generateCard(
                    cardStyle = "streak",
                    mainValue = streak.toString(),
                    subValue = if (streak == 1) "day of beginning again." else "$streak days of beginning again.",
                    kicker = "MORNINGS KEPT"
                )
                "ring" -> cardGenerator.generateCard(
                    cardStyle = "ring",
                    mainValue = "86%",
                    subValue = "Top 8% of early risers this month.",
                    kicker = "CONSISTENCY"
                )
                "badge" -> cardGenerator.generateCard(
                    cardStyle = "badge",
                    mainValue = "Early riser",
                    subValue = "Five mornings before 6 AM, in a row.",
                    kicker = "EARNED"
                )
                else -> cardGenerator.generateStreakCard(
                    streak = streak,
                    habitName = habitName,
                    description = _customDescription.value,
                    startDate = dateStr,
                    isMilestone = isMilestone(streak)
                )
            }
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
            "Telegram" -> createShareIntent("org.telegram.messenger", uri, caption)
            else -> createGenericShareIntent(uri, caption)
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        
        try {
            val chooser = if (platform == "More" || intent.`package` == null) {
                Intent.createChooser(intent, "Share with").apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            } else {
                intent
            }
            context.startActivity(chooser)
            trackShareEvent(platform, streak)
        } catch (e: Exception) {
            Log.e("Share", "Failed to share to $platform", e)
            // Final fallback to generic chooser without package
            try {
                val fallback = createGenericShareIntent(uri, caption).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(Intent.createChooser(fallback, "Share with").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            } catch (e2: Exception) {
                Log.e("Share", "Total failure sharing", e2)
            }
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
            "Twitter" -> "Day $streak of my morning routine! Consistency is key. #ShoshinApp #MorningHabits"
            else -> "Day $streak of my morning routine! I'm building consistency with Shoshin App. #MorningHabits"
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
