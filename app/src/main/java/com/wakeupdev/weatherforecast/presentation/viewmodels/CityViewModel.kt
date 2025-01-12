package com.wakeupdev.weatherforecast.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wakeupdev.weatherforecast.data.db.repo.CityRepository
import com.wakeupdev.weatherforecast.data.api.GeocodingApiService
import com.wakeupdev.weatherforecast.presentation.uistates.CityUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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