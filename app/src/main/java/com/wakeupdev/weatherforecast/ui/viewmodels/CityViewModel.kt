package com.wakeupdev.weatherforecast.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wakeupdev.weatherforecast.data.repos.CityRepository
import com.wakeupdev.weatherforecast.data.api.GeocodingApiService
import com.wakeupdev.weatherforecast.ui.CityUiState
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

    private val _favCities = MutableStateFlow<CityUiState>(CityUiState.Idle)
    val favCities get() = _favCities.asStateFlow()

    private val _citiesDataSearch = MutableStateFlow<CityUiState>(CityUiState.Idle)
    val citiesSearch get() = _citiesDataSearch.asStateFlow()

    fun searchCity(searchQuery: String) {
        viewModelScope.launch {
            _citiesDataSearch.value = CityUiState.Loading
            try {
                val citiesData = cityRepository.searchCity(searchQuery)
                _citiesDataSearch.value = CityUiState.Success(citiesData)
            } catch (e: Exception) {
                Log.e("CityViewModel", "searchCity: $e", )
                _citiesDataSearch.value = CityUiState.Error(e.localizedMessage)
            }
        }
    }

    private fun getFavoriteLocations(){
        viewModelScope.launch {
            _favCities.value = CityUiState.Loading
            try {
                val favoriteCities = cityRepository.getFavLocations()
                _favCities.value = CityUiState.Success(favoriteCities)
            } catch (e: Exception){
                Log.e("CityViewModel", "searchCity: $e", )
                _favCities.value = CityUiState.Error(e.localizedMessage)
            }
        }
    }

    fun saveFavoriteCity(){

    }

    fun clearSearchCitiesData() {
        // Update only the `Success` state to clear the cities list
        if (_citiesDataSearch.value is CityUiState.Success) {
            _citiesDataSearch.value = CityUiState.Success(emptyList())  // Clear cities list
        }
    }

    init {
        getFavoriteLocations()
    }
}