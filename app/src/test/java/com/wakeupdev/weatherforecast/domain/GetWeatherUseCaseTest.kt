package com.wakeupdev.weatherforecast.domain

import com.wakeupdev.weatherforecast.data.WeatherData
import com.wakeupdev.weatherforecast.data.db.entities.WeatherEntity
import com.wakeupdev.weatherforecast.data.db.entities.toWeatherData
import com.wakeupdev.weatherforecast.data.repos.WeatherRepository
import com.wakeupdev.weatherforecast.utils.Logger
import com.wakeupdev.weatherforecast.utils.MockLogger
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GetWeatherUseCaseTest {

    private lateinit var weatherRepository: WeatherRepository
    private lateinit var getWeatherUseCase: GetWeatherUseCase
    private val mockLogger = MockLogger()

    @Before
    fun setUp() {
        weatherRepository = mockk()
        getWeatherUseCase = GetWeatherUseCase(mockLogger, weatherRepository)
    }

    @Test
    fun `test getWeather returns local data and network data`() = runBlocking {
        val lat = 40.7128
        val lon = -74.0060
        val isCurrentCity = true

        // Mock local data (cached data from the database)
        val localWeatherEntity = WeatherEntity(
            cityName = "New York",
            currentTemperature = 20.0,
            weatherCondition = "Clear",  // Add all necessary fields
            weatherIcon = "clear_icon",  // Add necessary weather fields
            dailyForecast = "[]", // Empty JSON array for simplicity in this example
            hourlyForecast = "[]", // Empty JSON array for simplicity in this example
            minTemperature = 15.0,
            maxTemperature = 25.0,
            latitude = lat,
            longitude = lon,
            windSpeed = 5.0,
            humidity = 80.0,
            uvIndex = 0.5,
            pressure = 1012.0,
            visibility = 10.0,
            dewPoint = 10.0,
            cityId = 1234L
        )

        coEvery { weatherRepository.getCurrentWeather() } returns flowOf(localWeatherEntity)

        // Mock network data (fresh data from the API)
        val networkWeather = WeatherData(
            cityName = "New York",
            currentTemperature = 25.0,
            weatherCondition = "Clear",
            weatherIcon = "clear_icon",
            minTemperature = 20.0,
            maxTemperature = 30.0,
            latitude = lat,
            longitude = lon,
            windSpeed = 6.0,
            humidity = 75.0,
            uvIndex = 0.8,
            pressure = 1015.0,
            visibility = 12.0,
            dewPoint = 12.0,
            hourlyTemperature = emptyList(), // Use empty list as a placeholder
            dailyForecast = emptyList() // Use empty list as a placeholder
        )

        coEvery { weatherRepository.getWeatherFromApi(lat, lon) } returns networkWeather

        // Call the use case
        val resultFlow = getWeatherUseCase.getWeather(lat, lon, isCurrentCity)

        // Collect the emitted values and check them
        val emittedData = resultFlow.firstOrNull()

        // Verify that the flow emits the local data first
        assertNotNull(emittedData)
        assertEquals(localWeatherEntity.toWeatherData(), emittedData)

        // Verify that the repository methods were called as expected
        coVerify { weatherRepository.getCurrentWeather() }
        coVerify { weatherRepository.getWeatherFromApi(lat, lon) }
    }

    @Test
    fun `test getWeather handles network error`() = runBlocking {
        val lat = 40.7128
        val lon = -74.0060
        val isCurrentCity = true

        // Mock local data to return null (no cached data)
        coEvery { weatherRepository.getCurrentWeather() } returns flowOf(null)

        // Mock network error (exception thrown by the repository)
        coEvery { weatherRepository.getWeatherFromApi(lat, lon) } throws Exception("Network error")

        // Call the use case and check if it throws the expected exception
        try {
            getWeatherUseCase.getWeather(lat, lon, isCurrentCity).collect()
            fail("Expected exception to be thrown")
        } catch (e: Exception) {
            // Verify that the exception is thrown
            assertEquals("Network error", e.message)
        }

        // Verify that the repository methods were called as expected
        coVerify { weatherRepository.getCurrentWeather() }
        coVerify { weatherRepository.getWeatherFromApi(lat, lon) }
    }

    @Test
    fun `test getFavWeather handles network error`() = runBlocking {
        val lat = 40.7128
        val lon = -74.0060

        // Mock network error (exception thrown by the repository)
        coEvery { weatherRepository.getWeatherFromApi(lat, lon) } throws Exception("Network error")

        // Call the use case and check if it throws the expected exception
        try {
            getWeatherUseCase.getFavWeather(lat, lon).collect()
            fail("Expected exception to be thrown")
        } catch (e: Exception) {
            // Verify that the exception is thrown
            assertEquals("Network error", e.message)
        }

        // Verify that the repository method was called
        coVerify { weatherRepository.getWeatherFromApi(lat, lon) }
    }
}
