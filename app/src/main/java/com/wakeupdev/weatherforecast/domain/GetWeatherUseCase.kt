package com.wakeupdev.weatherforecast.domain

import android.util.Log
import com.wakeupdev.weatherforecast.data.WeatherData
import com.wakeupdev.weatherforecast.data.db.entities.toWeatherData
import com.wakeupdev.weatherforecast.data.repos.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class GetWeatherUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) {
    suspend fun getWeather(lat: Double, lon: Double): Flow<WeatherData> {
        return flow {
            val localData = weatherRepository.getWeatherFromDatabase().firstOrNull()
            localData?.let { emit(it.toWeatherData()) }

            // then attempt to get from network
            try {
                val networkWeather = weatherRepository.getWeatherFromApi(lat, lon)
                weatherRepository.cacheWeatherData(networkWeather)
                emit(networkWeather)
            } catch (e: Exception) {
                Log.e("WeatherData", "Error fetching data from network", e)
                throw e
            }
        }
            .catch { e ->
                Log.e("WeatherData", "Error in flow: ${e.message}")
                throw e  // Rethrow the error to propagate it
            }
    }
}
