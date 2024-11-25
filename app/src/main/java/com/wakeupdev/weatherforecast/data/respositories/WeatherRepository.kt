package com.wakeupdev.weatherforecast.data.respositories

import com.wakeupdev.weatherforecast.data.WeatherData
import com.wakeupdev.weatherforecast.data.api.WeatherApiService
import com.wakeupdev.weatherforecast.data.api.models.toWeatherData
import com.wakeupdev.weatherforecast.data.db.weather.WeatherDao
import com.wakeupdev.weatherforecast.data.db.weather.toDomainModel
import com.wakeupdev.weatherforecast.data.db.weather.toEntity
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val apiService: WeatherApiService,
    private val weatherDao: WeatherDao
) {

    suspend fun getWeatherData(lat: Double, lon: Double, cityName: String): WeatherData {
        return try {
            // Fetch from API
            val response = apiService.getWeather(lat, lon)
            val weatherData = response.toWeatherData()

            // Cache the result
            weatherDao.insertWeatherData(weatherData.toEntity())

            weatherData
        } catch (e: Exception) {
            // Fetch from cache if available
            val cachedData = weatherDao.getWeatherData(cityName)
            cachedData?.toDomainModel() ?: throw e
        }
    }
}
