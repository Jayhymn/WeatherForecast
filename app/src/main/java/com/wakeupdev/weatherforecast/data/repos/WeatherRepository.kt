package com.wakeupdev.weatherforecast.data.repos

import com.wakeupdev.weatherforecast.data.WeatherData
import com.wakeupdev.weatherforecast.data.api.WeatherApiService
import com.wakeupdev.weatherforecast.data.api.toWeatherData
import com.wakeupdev.weatherforecast.data.api.toWeatherEntity
import com.wakeupdev.weatherforecast.data.db.dao.CityDao
import com.wakeupdev.weatherforecast.data.db.dao.WeatherDao
import com.wakeupdev.weatherforecast.data.db.entities.WeatherEntity
import com.wakeupdev.weatherforecast.data.db.entities.toWeatherData
import com.wakeupdev.weatherforecast.data.toEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val apiService: WeatherApiService,
    private val weatherDao: WeatherDao,
    private val cityDao: CityDao,
) {
    fun getWeatherFromDatabase(): Flow<WeatherEntity?> {
        return weatherDao.fetchWeatherData()
    }

    suspend fun getWeatherFromApi(lat: Double, lon: Double): WeatherData {
        return try {
            // Fetch from API
            val response = apiService.getWeather(lat, lon)
            val weatherData = response.toWeatherData()

            // Cache the result
            weatherDao.insertWeatherData(weatherData.toEntity())

            weatherData
        } catch (e: Exception) {
            // get from cache if available
            val cachedData = weatherDao.getWeatherData(lat, lon)
            cachedData?.toWeatherData() ?: throw e
        }
    }

    suspend fun syncWeatherData() {
        // Example: Fetch and save data for all saved cities
        val favoriteCities = cityDao.getFavoriteCities()

        for (city in favoriteCities){
            val weather = apiService.getWeather(city.latitude, city.longitude)
            val weatherEntity = weather.toWeatherEntity()

            // update / save to database
            weatherDao.updateWeatherData(weatherEntity)
        }
    }

    suspend fun cacheWeatherData(weatherData: WeatherData) {
        weatherDao.insertWeatherData(weatherData.toEntity())
    }
}
