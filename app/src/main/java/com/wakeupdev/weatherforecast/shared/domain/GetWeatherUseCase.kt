package com.wakeupdev.weatherforecast.shared.domain

import com.wakeupdev.weatherforecast.features.weather.data.WeatherData
import com.wakeupdev.weatherforecast.features.weather.data.WeatherRepository
import javax.inject.Inject

class GetWeatherUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) {
    suspend operator fun invoke(lat: Double, lon: Double, cityName: String): WeatherData {
        return weatherRepository.getWeatherData(lat, lon, cityName)
    }
}
