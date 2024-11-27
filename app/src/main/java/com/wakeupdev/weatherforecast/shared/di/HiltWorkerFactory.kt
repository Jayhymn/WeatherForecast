package com.wakeupdev.weatherforecast.shared.di

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import javax.inject.Inject

class HiltWorkerFactory @Inject constructor(
    private val workerFactories: Set<@JvmSuppressWildcards WorkerFactory> // Inject all WorkerFactories
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        for (factory in workerFactories) {
            val worker = factory.createWorker(appContext, workerClassName, workerParameters)
            if (worker != null) {
                return worker
            }
        }
        return null
    }
}