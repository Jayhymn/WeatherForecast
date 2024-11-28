package com.wakeupdev.weatherforecast.data.repos

import com.wakeupdev.weatherforecast.data.api.City
import com.wakeupdev.weatherforecast.data.api.GeocodingApiService
import com.wakeupdev.weatherforecast.data.api.toCityData
import com.wakeupdev.weatherforecast.data.db.dao.CityDao
import com.wakeupdev.weatherforecast.data.db.dao.WeatherDao
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