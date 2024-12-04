package com.wakeupdev.weatherforecast.domain

import com.wakeupdev.weatherforecast.data.WeatherData
import com.wakeupdev.weatherforecast.data.db.entities.toWeatherData
import com.wakeupdev.weatherforecast.data.repos.WeatherRepository
import com.wakeupdev.weatherforecast.utils.Logger  // Import your Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class GetWeatherUseCase @Inject constructor(
    private val logger: Logger,
    private val weatherRepository: WeatherRepository
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
                // Use logger for error
                logger.e("WeatherData", "Error fetching data from network", e)
                throw e
            }
        }
            .catch { e ->
                // Use logger for error in flow
                logger.e("WeatherData", "Error in flow: ${e.message}", e)
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
                // Use logger for error
                logger.e("WeatherData", "Error fetching data from network", e)
                throw e
            }
        }
            .catch { e ->
                // Use logger for error in flow
                logger.e("WeatherData", "Error in flow: ${e.message}", e)
                throw e
            }
    }
}
