package com.wakeupdev.weatherforecast.features.city.presentation

import com.wakeupdev.weatherforecast.features.city.data.CityData

data class CityUiState(
    val citiesData: List<CityData> = emptyList(),
    val errorMessages: List<Int> = emptyList(),
    val isLoading: Boolean = false,
    val successMessage: String? = null
)