package com.example.shoshinapp.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import java.io.File

object ImageCompressor {
    
    /**
     * Compress bitmap to ~80KB or less
     * Quality: 60% JPEG compression
     * Resolution: 800x600 max
     */
    fun compressBitmap(bitmap: Bitmap): Bitmap {
        // Resize if too large
        val maxWidth = 800
        val maxHeight = 600
        
        return if (bitmap.width > maxWidth || bitmap.height > maxHeight) {
            val scale = minOf(
                maxWidth.toFloat() / bitmap.width,
                maxHeight.toFloat() / bitmap.height
            )
            val newWidth = (bitmap.width * scale).toInt()
            val newHeight = (bitmap.height * scale).toInt()
            Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
        } else {
            bitmap
        }
    }

    /**
     * Save compressed bitmap to file
     * Returns file path
     */
    fun saveBitmapToFile(bitmap: Bitmap, filePath: String): Result<String> {
        return try {
            val compressed = compressBitmap(bitmap)
            val file = File(filePath)
            file.parentFile?.mkdirs()
            
            file.outputStream().use { output ->
                compressed.compress(Bitmap.CompressFormat.JPEG, 60, output)
            }
            
            Result.success(filePath)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Load compressed bitmap from file
     */
    fun loadBitmapFromFile(filePath: String): Result<Bitmap> {
        return try {
            val file = File(filePath)
            if (!file.exists()) {
                return Result.failure(Exception("File not found"))
            }
            
            val bitmap = BitmapFactory.decodeFile(filePath)
            if (bitmap != null) {
                Result.success(bitmap)
            } else {
                Result.failure(Exception("Failed to decode bitmap"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get file size in KB
     */
    fun getFileSizeKB(filePath: String): Float {
        return File(filePath).length() / 1024f
    }

    /**
     * Delete compressed image
     */
    fun deleteCompressedImage(filePath: String): Boolean {
        return try {
            File(filePath).delete()
        } catch (e: Exception) {
            false
        }
    }
}
