package com.example.shoshinapp.utils

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
            
            // Check if any target label or similar concept matches
            val match = targets.any { target -> 
                detected.any { it.contains(target) || target.contains(it) }
            }

            if (match) {
                VerificationResult.Success(detected.firstOrNull { d -> targets.any { t -> d.contains(t) } } ?: "Match")
            } else {
                VerificationResult.Failure("Could not clearly identify ${targets.joinToString("/")}. Detected: ${detected.take(3).joinToString(", ")}")
            }
        } catch (e: Exception) {
            VerificationResult.Error(e.message ?: "Verification failed")
        }
    }
}

sealed class VerificationResult {
    data class Success(val label: String) : VerificationResult()
    data class Failure(val message: String) : VerificationResult()
    data class Error(val message: String) : VerificationResult()
}
