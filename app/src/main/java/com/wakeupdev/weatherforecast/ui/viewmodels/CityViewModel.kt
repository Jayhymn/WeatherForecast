package com.wakeupdev.weatherforecast.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wakeupdev.weatherforecast.data.WeatherData
import com.wakeupdev.weatherforecast.data.respositories.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CityViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    private val _weatherData = MutableLiveData<WeatherData>()
    val weatherData: LiveData<WeatherData> = _weatherData

    fun getWeather(lat: Double, lon: Double, cityName: String) {
        viewModelScope.launch {
            try {
                val data = weatherRepository.getWeatherData(lat, lon, cityName)
                _weatherData.value = data
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}