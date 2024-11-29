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
    private val weatherRepository: WeatherRepository,
) {
    suspend fun getWeather(lat: Double, lon: Double, isCurrentCity: Boolean): Flow<WeatherData> {
        return flow {
            val localData = weatherRepository.getCurrentWeather().firstOrNull()

            // Emit local data if available
            localData?.let {
                emit(it.toWeatherData())
            }

            try {
                // Fetch data from API
                val networkWeather = weatherRepository.getWeatherFromApi(lat, lon)
                weatherRepository.cacheWeatherData(networkWeather, isCurrentCity)

                emit(networkWeather)
            } catch (e: Exception) {
                Log.e("WeatherData", "Error fetching data from network", e)
                throw e
            }
        }
            .catch { e ->
                Log.e("WeatherData", "Error in flow: ${e.message}")
                throw e
            }
    }

    suspend fun getFavWeather(lat: Double, lon: Double): Flow<WeatherData> {
        return flow {
            try {
                // Fetch data from API
                val networkWeather = weatherRepository.getWeatherFromApi(lat, lon)

                emit(networkWeather)
            } catch (e: Exception) {
                Log.e("WeatherData", "Error fetching data from network", e)
                throw e
            }
        }
            .catch { e ->
                Log.e("WeatherData", "Error in flow: ${e.message}")
                throw e
            }
    }
}

