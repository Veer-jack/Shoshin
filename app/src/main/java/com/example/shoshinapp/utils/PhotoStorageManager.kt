package com.example.shoshinapp.utils

import android.content.Context
import android.graphics.Bitmap
import java.io.File
import java.util.UUID

class PhotoStorageManager(private val context: Context) {
    
    private val photoCacheDir: File
        get() {
            val dir = File(context.cacheDir, "shoshin_photos")
            if (!dir.exists()) {
                dir.mkdirs()
            }
            return dir
        }

    /**
     * Save compressed photo locally
     * Returns local file path
     */
    fun saveCompressedPhoto(bitmap: Bitmap): Result<String> {
        return try {
            val filename = "photo_${UUID.randomUUID()}.jpg"
            val filePath = File(photoCacheDir, filename).absolutePath
            
            ImageCompressor.saveBitmapToFile(bitmap, filePath)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Load compressed photo from local storage
     */
    fun loadCompressedPhoto(filePath: String): Result<Bitmap> {
        return ImageCompressor.loadBitmapFromFile(filePath)
    }

    /**
     * Delete compressed photo
     */
    fun deletePhoto(filePath: String): Boolean {
        return ImageCompressor.deleteCompressedImage(filePath)
    }

    /**
     * Get total cache size in MB
     */
    fun getCacheSizeMB(): Float {
        return photoCacheDir.listFiles()?.sumOf { it.length() }?.let { it / (1024f * 1024f) } ?: 0f
    }

    /**
     * Clear all cached photos
     */
    fun clearPhotoCache(): Boolean {
        return try {
            photoCacheDir.listFiles()?.forEach { it.delete() }
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Get list of all cached photos
     */
    fun getCachedPhotos(): List<File> {
        return photoCacheDir.listFiles()?.toList() ?: emptyList()
    }
}
