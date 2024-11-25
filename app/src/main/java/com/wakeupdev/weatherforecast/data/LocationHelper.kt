package com.wakeupdev.weatherforecast.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.tasks.await // This import is needed for await() to work
import javax.inject.Inject

class LocationHelper @Inject constructor(
    private val context: Context
) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    suspend fun getCurrentLocation(): Location? {
        // Check if permissions are granted
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Return null if permissions are not granted
            return null
        }

        // Try to get the last known location
        return try {
            fusedLocationClient.lastLocation.await() // Await the result of the location request
        } catch (e: Exception) {
            // Return null in case of any exception
            null
        }
    }
}
