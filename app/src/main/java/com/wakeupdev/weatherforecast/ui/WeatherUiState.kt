package com.wakeupdev.weatherforecast.ui

import com.wakeupdev.weatherforecast.data.WeatherData

sealed interface WeatherUiState {
    data object Idle : WeatherUiState
    data object Loading : WeatherUiState
    data class Success(val weatherData: WeatherData) : WeatherUiState
    data class Error(val message: String?) : WeatherUiState
}
