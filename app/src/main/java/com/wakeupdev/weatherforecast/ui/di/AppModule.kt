package com.wakeupdev.weatherforecast.ui.di

import android.content.Context
import androidx.room.Room
import com.wakeupdev.weatherforecast.Constants
import com.wakeupdev.weatherforecast.data.api.ApiKeyInterceptor
import com.wakeupdev.weatherforecast.data.api.WeatherApiService
import com.wakeupdev.weatherforecast.data.db.WeatherDb
import com.wakeupdev.weatherforecast.data.db.weather.WeatherDao
import com.wakeupdev.weatherforecast.data.respositories.WeatherRepository
import com.wakeupdev.weatherforecast.domain.GetWeatherUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideWeatherDb(
        @ApplicationContext context: Context
    ): WeatherDb {
        return Room.databaseBuilder(
            context,
            WeatherDb::class.java,
            "weather_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideWeatherDao(weatherDb: WeatherDb): WeatherDao {
        return weatherDb.weatherDao()
    }

    @Provides
    @Singleton
    fun provideWeatherApiService(): WeatherApiService {
        val httpClient = OkHttpClient.Builder()
            .addInterceptor(ApiKeyInterceptor()) // Custom interceptor to append API key
            .build()

        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL) // Base URL of the API
            .addConverterFactory(GsonConverterFactory.create()) // JSON parsing
            .client(httpClient) // Attach OkHttp client with the interceptor
            .build()
            .create(WeatherApiService::class.java) // Create WeatherApiService instance
    }

    @Provides
    @Singleton
    fun provideWeatherRepository(
        weatherApiService: WeatherApiService,
        weatherDao: WeatherDao
    ): WeatherRepository {
        return WeatherRepository(weatherApiService, weatherDao)
    }

    @Provides
    @Singleton
    fun provideGetWeatherUseCase(
        weatherRepository: WeatherRepository
    ): GetWeatherUseCase {
        return GetWeatherUseCase(weatherRepository)
    }
}
