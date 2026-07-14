package com.example.shoshinapp.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.Tasks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume

object LocationHelper {

    @SuppressLint("MissingPermission")
    suspend fun getLastLocation(context: Context): Location? = withContext(Dispatchers.IO) {
        try {
            val client = LocationServices.getFusedLocationProviderClient(context)
            val task = client.lastLocation
            Tasks.await(task)
        } catch (e: Exception) {
            null
        }
    }

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(context: Context): Location? {
        return withTimeoutOrNull(5000) {
            suspendCancellableCoroutine { continuation ->
                val client = LocationServices.getFusedLocationProviderClient(context)
                val request = LocationRequest.Builder(
                    Priority.PRIORITY_HIGH_ACCURACY, 1000
                ).setMaxUpdates(1).build()

                val callback = object : LocationCallback() {
                    override fun onLocationResult(result: LocationResult) {
                        client.removeLocationUpdates(this)
                        continuation.resume(result.lastLocation)
                    }
                }

                try {
                    client.requestLocationUpdates(
                        request,
                        callback,
                        Looper.getMainLooper()
                    )
                    continuation.invokeOnCancellation {
                        client.removeLocationUpdates(callback)
                    }
                } catch (e: Exception) {
                    continuation.resume(null)
                }
            }
        } ?: getLastLocation(context) // fallback to cached if timeout
    }

    fun roundForAnalytics(value: Double): Double {
        return Math.round(value * 100.0) / 100.0
    }
}
