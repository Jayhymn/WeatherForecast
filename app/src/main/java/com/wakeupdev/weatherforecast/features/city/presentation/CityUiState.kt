package com.wakeupdev.weatherforecast.features.city.presentation

import com.wakeupdev.weatherforecast.features.city.data.City
import com.wakeupdev.weatherforecast.features.weather.data.WeatherData
import com.wakeupdev.weatherforecast.features.weather.presentation.WeatherUiState

sealed interface CityUiState{
    data object Idle : CityUiState
    data object Loading : CityUiState
    data class Success(val citiesData: List<City>) : CityUiState
    data class Error(val message: String?) : CityUiState
}