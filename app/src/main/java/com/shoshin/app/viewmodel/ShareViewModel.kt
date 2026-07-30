package com.Shoshin.app.viewmodel

import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Shoshin.app.utils.ShareCardGenerator
import com.Shoshin.app.utils.AnalyticsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ShareViewModel(application: Application) : AndroidViewModel(application) {

    private val context: Context get() = getApplication<Application>().applicationContext
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
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.Default) {
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
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            val filename = "shoshin_share_${System.currentTimeMillis()}"
            val file = cardGenerator.saveBitmapToFile(bitmap, filename) ?: return@launch
            
            val uri = try {
                FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            } catch (e: Exception) {
                Log.e("Share", "Failed to get URI for file", e)
                return@launch
            }

            val caption = getCaption(platform, streak)
            
            val baseIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_TEXT, caption)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                // Best practice for granting URI permissions on Android 10+
                clipData = android.content.ClipData.newRawUri("", uri)
            }

            val targetPackage = when (platform) {
                "Instagram" -> "com.instagram.android"
                "Twitter" -> "com.twitter.android"
                "Facebook" -> "com.facebook.katana"
                "WhatsApp" -> "com.whatsapp"
                "Telegram" -> "org.telegram.messenger"
                "TikTok" -> "com.zhiliaoapp.musically"
                "Snapchat" -> "com.snapchat.android"
                else -> null
            }

            val finalIntent = if (targetPackage != null) {
                // Check if the target app can actually handle this intent
                val packageManager = context.packageManager
                val activities = packageManager.queryIntentActivities(baseIntent, android.content.pm.PackageManager.MATCH_DEFAULT_ONLY)
                val isHandlerInstalled = activities.any { it.activityInfo.packageName == targetPackage }
                
                if (isHandlerInstalled) {
                    baseIntent.setPackage(targetPackage)
                    baseIntent
                } else {
                    Intent.createChooser(baseIntent, "Share your practice")
                }
            } else {
                Intent.createChooser(baseIntent, "Share your practice")
            }

            finalIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            // Grant permission to the chooser intent as well if it's a chooser
            finalIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            
            try {
                context.startActivity(finalIntent)
                trackShareEvent(platform, streak)
            } catch (e: Exception) {
                Log.e("Share", "Failed to share to $platform. Error: ${e.message}", e)
                // Ultimate fallback
                try {
                    val fallbackIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "image/png"
                        putExtra(Intent.EXTRA_STREAM, uri)
                        putExtra(Intent.EXTRA_TEXT, getCaption("More", streak))
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    val chooser = Intent.createChooser(fallbackIntent, "Share with")
                    chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(chooser)
                } catch (e2: Exception) {
                    Log.e("Share", "Ultimate failure while sharing. Error: ${e2.message}", e2)
                }
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
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
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
