package com.example.shoshinapp.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Tasks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
}
