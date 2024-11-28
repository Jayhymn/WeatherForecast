package com.wakeupdev.weatherforecast

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LocationHelper @Inject constructor(
    private val context: Context
) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    suspend fun getCurrentLocation(): Location? {
        if (!hasLocationPermission()) {
            return null
        }

        // Try fetching last known location first
        val lastLocation = fusedLocationClient.lastLocation.await()
        if (lastLocation != null) {
            return lastLocation
        }

        return fetchFreshLocation()
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermissionIfNeeded(fragment: Fragment, requestCode: Int, onPermissionGranted: () -> Unit) {
        if (!hasLocationPermission()) {
            requestPermission(fragment, requestCode)
        } else {
            onPermissionGranted()
        }
    }


    private fun requestPermission(fragment: Fragment, requestCode: Int) {
        ActivityCompat.requestPermissions(
            fragment.requireActivity(),
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            requestCode
        )
    }

    private suspend fun fetchFreshLocation(): Location? {
        return try {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).await()
        } catch (e: Exception) {
            null
        }
    }
}
