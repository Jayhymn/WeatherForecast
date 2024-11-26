package com.wakeupdev.weatherforecast.features.city.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wakeupdev.weatherforecast.R
import com.wakeupdev.weatherforecast.features.city.data.CityRepository
import com.wakeupdev.weatherforecast.features.city.data.api.GeocodingApiService
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

    private val _citiesData = MutableStateFlow(CityUiState())
    val citiesData get() = _citiesData.asStateFlow()

    fun searchCity(searchQuery: String) {
        viewModelScope.launch {
            _citiesData.update { it.copy(isLoading = false) }
            try {
                val citiesData = cityRepository.searchCity(searchQuery)
                _citiesData.update { it.copy(citiesData = citiesData, isLoading = true) }
            } catch (e: Exception) {
                _citiesData.update {
                    it.copy(
                        errorMessages = it.errorMessages + R.string.load_error,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun saveFavoriteCity(){

    }
}