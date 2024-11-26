package com.wakeupdev.weatherforecast.features.weather

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.wakeupdev.weatherforecast.shared.utils.NotificationUtils
import com.wakeupdev.weatherforecast.features.weather.data.WeatherRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltWorker
class SyncWorker @Inject constructor(
    @ApplicationContext private val context: Context,
    workerParams: WorkerParameters, // Add workerParams here
    private val weatherRepository: WeatherRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        setForegroundAsync(NotificationUtils.createForegroundInfo(context, "refreshing weather forecast data"))

        return try {
            // Fetch weather data from the API and update the database
            weatherRepository.syncWeatherData()

            // Indicate success
            Result.success()
        } catch (e: Exception) {
            // Log or handle the exception here, and retry later if needed
            Log.e("SyncWorker", "Failed to sync weather data: ${e.message}")
            Result.retry()  // Retry the work in case of failure
        }
    }
}
