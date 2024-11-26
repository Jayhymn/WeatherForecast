package com.wakeupdev.weatherforecast.features.weather.data

import com.wakeupdev.weatherforecast.features.weather.data.api.WeatherApiService
import com.wakeupdev.weatherforecast.features.weather.data.api.toWeatherData
import com.wakeupdev.weatherforecast.features.weather.data.api.toWeatherEntity
import com.wakeupdev.weatherforecast.features.city.data.db.CityDao
import com.wakeupdev.weatherforecast.features.weather.data.db.WeatherDao
import com.wakeupdev.weatherforecast.features.weather.data.db.toDomainModel
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
            val cachedData = weatherDao.getWeatherData(cityName)
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
