package com.wakeupdev.weatherforecast.features.city.presentation

import com.wakeupdev.weatherforecast.features.city.data.City

data class CityUiState(
    val citiesData: List<City> = emptyList(),
    val errorMessages: List<Int> = emptyList(),
    val isLoading: Boolean = false,
    val successMessage: String? = null
)