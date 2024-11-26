package com.wakeupdev.weatherforecast.features.weather.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wakeupdev.weatherforecast.R
import com.wakeupdev.weatherforecast.shared.domain.GetWeatherUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getWeatherUseCase: GetWeatherUseCase
) : ViewModel() {
    private val _weatherState = MutableStateFlow<WeatherUiState>(WeatherUiState.Idle)
    val weatherState get() = _weatherState.asStateFlow()

    fun fetchWeatherForCity(lat: Double, lon: Double, cityName: String) {
        viewModelScope.launch {
            _weatherState.value = WeatherUiState.Loading
            try {
                val weatherData = getWeatherUseCase(lat, lon, cityName)
                _weatherState.value = WeatherUiState.Success(weatherData)
            } catch (e: Exception) {
                _weatherState.value = WeatherUiState.Error(e.localizedMessage)
            }
        }
    }

    init {

    }
}


