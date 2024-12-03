package com.wakeupdev.weatherforecast.sync

import android.content.Context
import androidx.work.WorkerParameters

interface WeatherSyncWorkerFactory {
    fun create(context: Context, workerParams: WorkerParameters): WeatherSyncWorker
}