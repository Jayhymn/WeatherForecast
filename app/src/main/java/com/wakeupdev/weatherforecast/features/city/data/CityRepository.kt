package com.wakeupdev.weatherforecast.features.city.data

import com.wakeupdev.weatherforecast.features.city.data.api.GeocodingApiService
import com.wakeupdev.weatherforecast.features.city.data.api.toCityData
import com.wakeupdev.weatherforecast.features.city.data.db.CityDao
import com.wakeupdev.weatherforecast.features.weather.data.db.WeatherDao
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
}