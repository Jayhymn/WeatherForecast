package com.wakeupdev.weatherforecast.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wakeupdev.weatherforecast.domain.GetWeatherUseCase
import com.wakeupdev.weatherforecast.ui.state.WeatherState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getWeatherUseCase: GetWeatherUseCase
) : ViewModel() {
    private val _weatherState = MutableLiveData<WeatherState>()
    val weatherState: LiveData<WeatherState> get() = _weatherState

    fun fetchWeather(lat: Double, lon: Double, cityName: String) {
        viewModelScope.launch {
            _weatherState.value = WeatherState.Loading
            try {
                val weatherData = getWeatherUseCase(lat, lon, cityName)
                _weatherState.value = WeatherState.Success(weatherData)
            } catch (e: Exception) {
                _weatherState.value = WeatherState.Error(e.message)
            }
        }
    }
}

