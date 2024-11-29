package com.wakeupdev.weatherforecast

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.wakeupdev.weatherforecast.data.api.City
import kotlinx.coroutines.tasks.await
import java.util.Locale
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

    fun reverseGeoCode(lat:Double, lon: Double): City {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addressList = geocoder.getFromLocation(lat, lon, 1)

        val address = addressList?.get(0)

        return City(
            id = 0,
            name = address?.locality ?: "",
            country = address?.countryName ?: "",
            longitude = lon,
            latitude = lat
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
