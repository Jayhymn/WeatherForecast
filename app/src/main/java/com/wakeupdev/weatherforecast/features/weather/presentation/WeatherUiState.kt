package com.wakeupdev.weatherforecast.features.weather.presentation

import com.wakeupdev.weatherforecast.features.weather.data.WeatherData

sealed interface WeatherUiState {
    data object Idle : WeatherUiState
    data object Loading : WeatherUiState
    data class Success(val weatherData: WeatherData) : WeatherUiState
    data class Error(val message: String?) : WeatherUiState
}
