package com.wakeupdev.weatherforecast.shared.di

import android.content.Context
import androidx.room.Room
import com.wakeupdev.weatherforecast.shared.Constants
import com.wakeupdev.weatherforecast.shared.ApiKeyInterceptor
import com.wakeupdev.weatherforecast.features.city.data.api.GeocodingApiService
import com.wakeupdev.weatherforecast.features.weather.data.api.WeatherApiService
import com.wakeupdev.weatherforecast.shared.LocalDatabase
import com.wakeupdev.weatherforecast.features.city.data.db.CityDao
import com.wakeupdev.weatherforecast.features.weather.data.db.WeatherDao
import com.wakeupdev.weatherforecast.features.city.data.CityRepository
import com.wakeupdev.weatherforecast.features.weather.data.WeatherRepository
import com.wakeupdev.weatherforecast.shared.domain.FormatDateUseCase
import com.wakeupdev.weatherforecast.shared.domain.GetWeatherUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideLocalDatabase(
        @ApplicationContext context: Context
    ): LocalDatabase {
        return Room.databaseBuilder(
            context,
            LocalDatabase::class.java,
            "weather_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideWeatherDao(weatherDb: LocalDatabase): WeatherDao {
        return weatherDb.weatherDao()
    }

    @Provides
    @Singleton
    fun provideCityDao(weatherDb: LocalDatabase): CityDao {
        return weatherDb.cityDao()
    }

    @Provides
    @Singleton
    fun provideWeatherApiService(): WeatherApiService {
        val httpClient = OkHttpClient.Builder()
            .connectTimeout(Constants.REQUEST_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(Constants.REQUEST_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(Constants.REQUEST_TIMEOUT, TimeUnit.SECONDS)
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
        weatherDao: WeatherDao,
        cityDao: CityDao,
    ): WeatherRepository {
        return WeatherRepository(weatherApiService, weatherDao, cityDao)
    }

    @Provides
    @Singleton
    fun provideCityRepository(
        geocodingApiService: GeocodingApiService,
        weatherDao: WeatherDao,
        cityDao: CityDao,
    ): CityRepository {
        return CityRepository(geocodingApiService, weatherDao, cityDao)
    }

    @Provides
    @Singleton
    fun provideGetWeatherUseCase(
        weatherRepository: WeatherRepository
    ): GetWeatherUseCase {
        return GetWeatherUseCase(weatherRepository)
    }

    @Provides
    @Singleton
    fun provideFormatDateUseCase(): FormatDateUseCase {
        return FormatDateUseCase()
    }
}
