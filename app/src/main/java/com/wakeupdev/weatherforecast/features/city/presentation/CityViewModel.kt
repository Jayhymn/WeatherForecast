package com.wakeupdev.weatherforecast.features.city.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wakeupdev.weatherforecast.R
import com.wakeupdev.weatherforecast.features.city.data.CityRepository
import com.wakeupdev.weatherforecast.features.city.data.api.GeocodingApiService
import com.wakeupdev.weatherforecast.features.weather.presentation.WeatherUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CityViewModel @Inject constructor(
    private val geocodingApiService: GeocodingApiService,
    private val cityRepository: CityRepository,
) : ViewModel() {

    private val _citiesData = MutableStateFlow<CityUiState>(CityUiState.Idle)
    val citiesData get() = _citiesData.asStateFlow()

    fun searchCity(searchQuery: String) {
        viewModelScope.launch {
            _citiesData.value = CityUiState.Loading
            try {
                val citiesData = cityRepository.searchCity(searchQuery)
                _citiesData.value = CityUiState.Success(citiesData)
            } catch (e: Exception) {
                Log.e("CityViewModel", "searchCity: $e", )
                _citiesData.value = CityUiState.Error(e.localizedMessage)
            }
        }
    }

    fun saveFavoriteCity(){

    }

    fun clearCitiesData() {
        // Update only the `Success` state to clear the cities list
        if (_citiesData.value is CityUiState.Success) {
            _citiesData.value = CityUiState.Success(emptyList())  // Clear cities list
        }
    }

}