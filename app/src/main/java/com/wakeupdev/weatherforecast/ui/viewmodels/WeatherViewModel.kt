package com.wakeupdev.weatherforecast.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wakeupdev.weatherforecast.domain.GetWeatherUseCase
import com.wakeupdev.weatherforecast.ui.state.WeatherUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getWeatherUseCase: GetWeatherUseCase
) : ViewModel() {
    private val _weatherState = MutableStateFlow<WeatherUiState>(WeatherUiState.Idle)
    val weatherState: StateFlow<WeatherUiState> get() = _weatherState

    fun fetchWeather(lat: Double, lon: Double, cityName: String) {
        viewModelScope.launch {
            _weatherState.value = WeatherUiState.Loading
            try {
                val weatherData = getWeatherUseCase(lat, lon, cityName)
                _weatherState.value = WeatherUiState.Success(weatherData)
            } catch (e: Exception) {
                _weatherState.value = WeatherUiState.Error(e.message)
            }
        }
    }
}

//lifecycleScope.launchWhenStarted {
//    viewModel.weatherState.collect { state ->
//        when (state) {
//            is WeatherState.Idle -> {
//                // Handle Idle state if needed
//            }
//            is WeatherState.Loading -> {
//                // Show loading indicator
//            }
//            is WeatherState.Success -> {
//                // Display weather data
//                val weatherData = state.data
//            }
//            is WeatherState.Error -> {
//                // Show error message
//                val errorMessage = state.message
//            }
//        }
//    }
//}


