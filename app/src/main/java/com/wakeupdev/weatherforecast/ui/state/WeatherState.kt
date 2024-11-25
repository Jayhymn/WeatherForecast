package com.wakeupdev.weatherforecast.ui.state

import com.wakeupdev.weatherforecast.data.WeatherData

sealed class WeatherState {
    data object Loading : WeatherState()
    data class Success(val weatherData: WeatherData) : WeatherState()
    data class Error(val message: String?) : WeatherState()
}
