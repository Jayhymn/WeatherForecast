package com.wakeupdev.weatherforecast.presentation.uistates

import com.wakeupdev.weatherforecast.data.City

sealed interface CityUiState{
    data object Idle : CityUiState
    data object Loading : CityUiState
    data class Success(val citiesData: List<City>) : CityUiState
    data class Error(val message: String?) : CityUiState
}