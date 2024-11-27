package com.wakeupdev.weatherforecast.shared.domain

import com.wakeupdev.weatherforecast.features.city.data.City
import com.wakeupdev.weatherforecast.features.city.data.CityRepository
import com.wakeupdev.weatherforecast.features.weather.data.WeatherData
import com.wakeupdev.weatherforecast.features.weather.data.WeatherRepository
import javax.inject.Inject

class GetCityUseCase @Inject constructor(
    private val cityRepository: CityRepository
) {
    suspend operator fun invoke(searchQuery: String): List<City> {
        return cityRepository.searchCity(searchQuery)
    }
}