package com.Shoshin.app.utils

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import kotlinx.coroutines.tasks.await

object ImageVerificationManager {

    private val labeler = ImageLabeling.getClient(
        ImageLabelerOptions.Builder()
            .setConfidenceThreshold(0.5f)
            .build()
    )

    /**
     * Verifies if the captured bitmap contains any of the target labels.
     * Common labels: "Sink", "Toothbrush", "Tree", "Book", "Person" (for selfie)
     */
    suspend fun verifyImage(bitmap: Bitmap, targetLabels: List<String>): VerificationResult {
        return try {
            val image = InputImage.fromBitmap(bitmap, 0)
            val labels = labeler.process(image).await()
            
            val detected = labels.map { it.text.lowercase() }
            val targets = targetLabels.map { it.lowercase() }
            
            android.util.Log.d("ImageVerification", "Detected: $detected")
            android.util.Log.d("ImageVerification", "Targets: $targets")
            
            // Check if any target label or similar concept matches
            val matchedImageLabel = labels.firstOrNull { l ->
                val text = l.text.lowercase()
                targets.any { target -> text.contains(target) || target.contains(text) }
            }

            if (matchedImageLabel != null) {
                android.util.Log.d("ImageVerification", "Match found: ${matchedImageLabel.text}")
                VerificationResult.Success(matchedImageLabel.text, matchedImageLabel.confidence)
            } else {
                android.util.Log.d("ImageVerification", "No match found")
                val topConfidence = labels.maxOfOrNull { it.confidence } ?: 0f
                VerificationResult.Failure(
                    "Could not clearly identify ${targets.joinToString("/")}. Detected: ${detected.take(3).joinToString(", ")}",
                    topConfidence
                )
            }
        } catch (e: Exception) {
            android.util.Log.e("ImageVerification", "Error during verification", e)
            VerificationResult.Error(e.message ?: "Verification failed")
        }
    }
}

sealed class VerificationResult {
    data class Success(val label: String, val confidence: Float = 0f) : VerificationResult()
    data class Failure(val message: String, val confidence: Float = 0f) : VerificationResult()
    data class Error(val message: String) : VerificationResult()
    data class AutoAccepted(val attemptNumber: Int) : VerificationResult()
}
