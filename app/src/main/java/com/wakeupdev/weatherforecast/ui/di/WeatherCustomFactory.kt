package com.wakeupdev.weatherforecast.ui.di

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.wakeupdev.weatherforecast.data.repos.WeatherRepository
import com.wakeupdev.weatherforecast.sync.WeatherSyncWorker
import javax.inject.Inject

class WeatherCustomFactory @Inject constructor(
    private val weatherRepository: WeatherRepository // Inject the repository or any other dependencies
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            WeatherSyncWorker::class.java.name -> {
                // Return your custom worker, providing the necessary parameters
                WeatherSyncWorker(appContext, workerParameters, weatherRepository)
            }
            else -> null // Return null if the worker class doesn't match
        }
    }
}