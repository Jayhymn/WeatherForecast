package com.wakeupdev.weatherforecast.features.weather.presentation

import com.wakeupdev.weatherforecast.features.weather.data.WeatherData

data class WeatherUiState(
    val weatherData: WeatherData? = null,
    val errorMessages: List<Int> = emptyList(),
    val isLoading: Boolean = false,
    val successMessage: String? = null
)
