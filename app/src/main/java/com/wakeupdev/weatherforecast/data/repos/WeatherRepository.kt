package com.wakeupdev.weatherforecast.data.repos

import com.wakeupdev.weatherforecast.data.WeatherData
import com.wakeupdev.weatherforecast.data.api.WeatherApiService
import com.wakeupdev.weatherforecast.data.api.toWeatherData
import com.wakeupdev.weatherforecast.data.api.toWeatherEntity
import com.wakeupdev.weatherforecast.data.db.dao.CityDao
import com.wakeupdev.weatherforecast.data.db.dao.WeatherDao
import com.wakeupdev.weatherforecast.data.db.entities.toDomainModel
import com.wakeupdev.weatherforecast.data.toEntity
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val apiService: WeatherApiService,
    private val weatherDao: WeatherDao,
    private val cityDao: CityDao,
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
            val cachedData = weatherDao.getWeatherData(lat, lon)
            cachedData?.toDomainModel() ?: throw e
        }
    }

    suspend fun syncWeatherData() {
        // Example: Fetch and save data for all saved cities
        val favoriteCities = cityDao.getAllCities()

        for (city in favoriteCities){
            val weather = apiService.getWeather(city.latitude, city.longitude)
            val weatherEntity = weather.toWeatherEntity()

            // update / save to database
            weatherDao.updateWeatherData(
                weatherEntity.cityName,
                weatherEntity.currentTemperature,
                weatherEntity.weatherCondition,
                weatherEntity.dailyForecast
            )
        }
    }
}
