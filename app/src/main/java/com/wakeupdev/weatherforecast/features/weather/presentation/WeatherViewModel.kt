package com.wakeupdev.weatherforecast.features.weather.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wakeupdev.weatherforecast.R
import com.wakeupdev.weatherforecast.shared.domain.GetWeatherUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getWeatherUseCase: GetWeatherUseCase
) : ViewModel() {
    private val _weatherState = MutableStateFlow(WeatherUiState())
    val weatherState get() = _weatherState.asStateFlow()

    fun fetchWeatherData(lat: Double, lon: Double, cityName: String) {
        viewModelScope.launch {
            _weatherState.update { it.copy(isLoading = true) }
            try {
                val weatherData = getWeatherUseCase(lat, lon, cityName)
                _weatherState.update { it.copy(weatherData = weatherData, isLoading = false) }
            } catch (e: Exception) {
                _weatherState.update {
                    it.copy(
                        errorMessages = it.errorMessages + R.string.load_error,
                        isLoading = false
                    )
                }
            }
        }
    }

    init {

    }
}


