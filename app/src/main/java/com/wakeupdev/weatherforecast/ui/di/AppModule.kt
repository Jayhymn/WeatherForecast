package com.wakeupdev.weatherforecast.ui.di

import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.room.Room
import androidx.work.WorkerFactory
import com.wakeupdev.weatherforecast.BuildConfig
import com.wakeupdev.weatherforecast.data.repos.CityRepository
import com.wakeupdev.weatherforecast.data.api.GeocodingApiService
import com.wakeupdev.weatherforecast.data.db.dao.CityDao
import com.wakeupdev.weatherforecast.data.repos.WeatherRepository
import com.wakeupdev.weatherforecast.data.api.WeatherApiService
import com.wakeupdev.weatherforecast.data.db.dao.WeatherDao
import com.wakeupdev.weatherforecast.ApiKeyInterceptor
import com.wakeupdev.weatherforecast.Constants
import com.wakeupdev.weatherforecast.data.LocalDatabase
import com.wakeupdev.weatherforecast.domain.FormatDateUseCase
import com.wakeupdev.weatherforecast.domain.GetCityUseCase
import com.wakeupdev.weatherforecast.domain.GetWeatherUseCase
import com.wakeupdev.weatherforecast.utils.Logger
import com.wakeupdev.weatherforecast.utils.LoggerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
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
    fun provideLogInterceptort(): HttpLoggingInterceptor {
        val loggingInterceptor = HttpLoggingInterceptor()

        if (BuildConfig.DEBUG) {
            loggingInterceptor
                .setLevel(HttpLoggingInterceptor.Level.HEADERS)
                .setLevel(HttpLoggingInterceptor.Level.BODY)
        } else {
            loggingInterceptor
                .setLevel(HttpLoggingInterceptor.Level.HEADERS)
        }

        return loggingInterceptor
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(Constants.REQUEST_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(Constants.REQUEST_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(Constants.REQUEST_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(ApiKeyInterceptor()) // Custom interceptor to append API key
            .addInterceptor(loggingInterceptor) // Add logging interceptor
            .addInterceptor { chain ->
                val original = chain.request()
                val request = original.newBuilder()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("accept", "application/json")
                    .method(original.method, original.body)
                    .build()
                chain.proceed(request)
            }
            .build()

    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL) // Base URL of the API
            .addConverterFactory(GsonConverterFactory.create()) // JSON parsing
            .client(okHttpClient) // Attach OkHttp client with the interceptor
            .build()
    }

    @Provides
    @Singleton
    fun provideWeatherApiService(retrofit: Retrofit): WeatherApiService {
        return retrofit.create(WeatherApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideGeocodingApiService(retrofit: Retrofit): GeocodingApiService {
        return retrofit.create(GeocodingApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideLogger(): Logger {
        return LoggerImpl()
    }


    @Provides
    @Singleton
    fun provideWeatherRepository(
        weatherApiService: WeatherApiService,
        weatherDao: WeatherDao,
        cityDao: CityDao,
        logger: Logger
    ): WeatherRepository {
        return WeatherRepository(weatherApiService, weatherDao, cityDao, logger)
    }

    @Provides
    @Singleton
    fun provideCityRepository(
        geocodingApiService: GeocodingApiService,
        weatherDao: WeatherDao,
        cityDao: CityDao,
        logger: Logger
    ): CityRepository {
        return CityRepository(geocodingApiService, weatherDao, cityDao, logger)
    }

    @Provides
    @Singleton
    fun provideGetWeatherUseCase(
        logger: Logger,
        weatherRepository: WeatherRepository
    ): GetWeatherUseCase {
        return GetWeatherUseCase(logger, weatherRepository)
    }

    @Provides
    @Singleton
    fun provideGetCityUseCase(
        cityRepository: CityRepository
    ): GetCityUseCase {
        return GetCityUseCase(cityRepository)
    }

    @Provides
    @Singleton
    fun provideFormatDateUseCase(
        logger: Logger
    ): FormatDateUseCase {
        return FormatDateUseCase(logger)
    }

    @Provides
    @Singleton
    fun provideWorkerFactory(hiltWorkerFactory: HiltWorkerFactory): WorkerFactory {
        return hiltWorkerFactory
    }
}
