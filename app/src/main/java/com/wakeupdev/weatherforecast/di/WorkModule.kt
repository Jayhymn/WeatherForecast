package com.wakeupdev.weatherforecast.di

import androidx.work.WorkerFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WorkerModule {

    @Provides
    @Singleton
    fun provideWorkerFactory(hiltWorkerFactory: HiltWorkerFactory): WorkerFactory {
        return hiltWorkerFactory
    }
}
