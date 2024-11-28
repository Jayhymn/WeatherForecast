package com.wakeupdev.weatherforecast.domain

import com.wakeupdev.weatherforecast.data.WeatherData
import com.wakeupdev.weatherforecast.data.repos.WeatherRepository
import javax.inject.Inject

class GetWeatherUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) {
    suspend operator fun invoke(lat: Double, lon: Double, cityName: String): WeatherData {
        return weatherRepository.getWeatherData(lat, lon, cityName)
    }
}
