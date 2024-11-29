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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CityRepository @Inject constructor(
    private val apiService: GeocodingApiService,
    private val weatherDao: WeatherDao,
    private val cityDao: CityDao,
) {
    suspend fun searchCity(searchQuery: String): List<City> {
        val cities = apiService.getGeocodes(searchQuery)

        return cities.map { it.toCityData() }
    }

    fun getFavCities(): Flow<List<City>> = cityDao.streamFavoriteCities().map {
        cityEntities -> cityEntities.map { it.toCityData() }
    }

    suspend fun saveFavCity(city: City, weatherDataList: WeatherData): Long{
        val result = cityDao.insert(city.toCityEntity())
        if (result != 0L){
            val weatherData = weatherDataList.toEntity().copy(id = result)
            weatherDao.insertWeatherData(weatherData)
        }

        return result
    }

    suspend fun deleteCities(cities: List<City>) {
        cityDao.deleteCities(cities.map { it.id })  // Assume you delete by city name
    }
}