package com.wakeupdev.weatherforecast.data.repos

import com.wakeupdev.weatherforecast.data.WeatherData
import com.wakeupdev.weatherforecast.data.api.City
import com.wakeupdev.weatherforecast.data.api.GeocodingApiService
import com.wakeupdev.weatherforecast.data.api.toCityData
import com.wakeupdev.weatherforecast.data.api.toCityEntity
import com.wakeupdev.weatherforecast.data.db.dao.CityDao
import com.wakeupdev.weatherforecast.data.db.dao.WeatherDao
import com.wakeupdev.weatherforecast.data.db.entities.toCityData
import com.wakeupdev.weatherforecast.data.toEntity
import com.wakeupdev.weatherforecast.utils.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CityRepository @Inject constructor(
    private val apiService: GeocodingApiService,
    private val weatherDao: WeatherDao,
    private val cityDao: CityDao,
    private val logger: Logger // Inject logger for error logging
) {
    suspend fun searchCity(searchQuery: String): List<City> {
        return try {
            val cities = apiService.getGeocodes(searchQuery)
            cities.map { it.toCityData() }
        } catch (e: Exception) {
            logger.e("CityRepository", "Error searching for city: $searchQuery", e)
            emptyList() // Return empty list in case of error
        }
    }

    fun getFavCities(): Flow<List<City>> = cityDao.streamFavoriteCities().map { cityEntities ->
        cityEntities.map { it.toCityData() }
    }

    suspend fun saveFavCity(city: City, weatherDataList: WeatherData): Long {
        return try {
            val result = cityDao.insert(city.toCityEntity())
            if (result != 0L) {
                val weatherData = weatherDataList.toEntity().copy(id = result)
                weatherDao.insertWeatherData(weatherData)
            }
            result
        } catch (e: Exception) {
            logger.e("CityRepository", "Error saving city: ${city.name}", e)
            0L // Return 0L in case of error
        }
    }

    suspend fun deleteCities(cities: List<City>) {
        try {
            cityDao.deleteCities(cities.map { it.id })
        } catch (e: Exception) {
            logger.e("CityRepository", "Error deleting cities: ${cities.joinToString { it.name }}", e)
        }
    }
}