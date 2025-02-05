package com.wakeupdev.weatherforecast.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wakeupdev.weatherforecast.domain.GetWeatherUseCase
import com.wakeupdev.weatherforecast.presentation.uistates.WeatherUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getWeatherUseCase: GetWeatherUseCase
) : ViewModel() {
    private val _weatherState = MutableStateFlow<WeatherUiState>(WeatherUiState.Idle)
    val weatherState = _weatherState.asStateFlow()

    fun fetchWeatherForCity(lat: Double, lon: Double, cityName: String, isCurrentCity: Boolean = true) {
        viewModelScope.launch {
            _weatherState.value = WeatherUiState.Loading
            try {
                // Stream updates from local and network sources
                getWeatherUseCase.getWeather(lat, lon, isCurrentCity)
                    .collect { weatherData ->
                        _weatherState.value = WeatherUiState.Success(weatherData)
                    }
            } catch (e: Exception) {
                _weatherState.value = WeatherUiState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun fetchFavWeather(lat: Double, lon: Double) {
        viewModelScope.launch {
            _weatherState.value = WeatherUiState.Loading
            try {
                // Stream updates from local and network sources
                getWeatherUseCase.getFavWeather(lat, lon)
                    .collect { weatherData ->
                        _weatherState.value = WeatherUiState.Success(weatherData)
                    }
            } catch (e: Exception) {
                _weatherState.value = WeatherUiState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }
}



