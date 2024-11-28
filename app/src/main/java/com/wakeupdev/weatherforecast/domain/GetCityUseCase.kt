package com.wakeupdev.weatherforecast.domain

import com.wakeupdev.weatherforecast.data.api.City
import com.wakeupdev.weatherforecast.data.repos.CityRepository
import javax.inject.Inject

class GetCityUseCase @Inject constructor(
    private val cityRepository: CityRepository
) {
    suspend operator fun invoke(searchQuery: String): List<City> {
        return cityRepository.searchCity(searchQuery)
    }
}