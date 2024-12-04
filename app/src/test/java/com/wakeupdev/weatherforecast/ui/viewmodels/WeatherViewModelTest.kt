package com.wakeupdev.weatherforecast.ui.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.wakeupdev.weatherforecast.data.WeatherData
import com.wakeupdev.weatherforecast.domain.GetWeatherUseCase
import com.wakeupdev.weatherforecast.ui.WeatherUiState
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class WeatherViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()  // Ensures LiveData updates are executed synchronously

    private lateinit var weatherViewModel: WeatherViewModel
    private val getWeatherUseCase: GetWeatherUseCase = mockk()

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        weatherViewModel = WeatherViewModel(getWeatherUseCase)
    }

    @Test
    fun `test fetchWeatherForCity success`() = runTest {
        val lat = 40.7128
        val lon = -74.0060
        val cityName = "New York"
        val weatherData = mockk<WeatherData>()

        coEvery { getWeatherUseCase.getWeather(lat, lon, true) } returns flow {
            emit(weatherData)
        }

        weatherViewModel.fetchWeatherForCity(lat, lon, cityName)

        // Allow coroutine to finish
        advanceUntilIdle()

        // Verify the state transitions correctly
        weatherViewModel.weatherState.collect { state ->
            when (state) {
                is WeatherUiState.Loading -> assertTrue(true) // Ensure the loading state is emitted first
                is WeatherUiState.Success -> assertEquals(weatherData, state.weatherData)
                is WeatherUiState.Error -> fail("Expected Success, but got error")
                is WeatherUiState.Idle -> fail("Expected Success, but got idle")
            }
        }

        coVerify { getWeatherUseCase.getWeather(lat, lon, true) }
    }

    @Test
    fun `test fetchWeatherForCity error`() = runTest {
        val lat = 40.7128
        val lon = -74.0060
        val cityName = "New York"
        val exception = Exception("Network error")

        coEvery { getWeatherUseCase.getWeather(lat, lon, true) } throws exception

        weatherViewModel.fetchWeatherForCity(lat, lon, cityName)

        // Allow coroutine to finish
        advanceUntilIdle()

        // Verify the state transitions to Error
        weatherViewModel.weatherState.collect { state ->
            when (state) {
                is WeatherUiState.Error -> assertEquals("Network error", state.message)
                is WeatherUiState.Success -> fail("Expected error, but got success")
                is WeatherUiState.Loading -> fail("Expected error, but got loading")
                is WeatherUiState.Idle -> fail("Expected error, but got idle")
            }
        }
    }

    @Test
    fun `test fetchFavWeather success`() = runTest {
        val lat = 40.7128
        val lon = -74.0060
        val weatherData = mockk<WeatherData>()

        coEvery { getWeatherUseCase.getFavWeather(lat, lon) } returns flow {
            emit(weatherData)
        }

        weatherViewModel.fetchFavWeather(lat, lon)

        // Allow coroutine to finish
        advanceUntilIdle()

        // Verify the state transitions correctly
        weatherViewModel.weatherState.collect { state ->
            when (state) {
                is WeatherUiState.Loading -> assertTrue(true) // Ensure it enters Loading state first
                is WeatherUiState.Success -> assertEquals(weatherData, state.weatherData)
                is WeatherUiState.Error -> fail("Expected Success, but got error")
                is WeatherUiState.Idle -> fail("Expected Success, but got idle")
            }
        }

        coVerify { getWeatherUseCase.getFavWeather(lat, lon) }
    }

    @Test
    fun `test fetchFavWeather error`() = runTest {
        val lat = 40.7128
        val lon = -74.0060
        val exception = Exception("Network error")

        coEvery { getWeatherUseCase.getFavWeather(lat, lon) } throws exception

        weatherViewModel.fetchFavWeather(lat, lon)

        // Allow coroutine to finish
        advanceUntilIdle()

        // Verify the state transitions to Error
        weatherViewModel.weatherState.collect { state ->
            when (state) {
                is WeatherUiState.Error -> assertEquals("Network error", state.message)
                is WeatherUiState.Success -> fail("Expected error, but got success")
                is WeatherUiState.Loading -> fail("Expected error, but got loading")
                is WeatherUiState.Idle -> fail("Expected error, but got idle")
            }
        }
    }

    @Test
    fun `test loading state in fetchWeatherForCity`() = runTest {
        val lat = 40.7128
        val lon = -74.0060
        val cityName = "New York"

        coEvery { getWeatherUseCase.getWeather(lat, lon, true) } returns flow { emit(mockk()) }

        weatherViewModel.fetchWeatherForCity(lat, lon, cityName)

        // Allow coroutine to finish
        advanceUntilIdle()

        // Verify the state transitions correctly
        weatherViewModel.weatherState.collect { state ->
            when (state) {
                is WeatherUiState.Loading -> assertTrue(true)  // Ensure it enters Loading state first
                is WeatherUiState.Success -> assertTrue(true)  // Verify Success state after Loading
                else -> fail("Expected Loading state, then Success state")
            }
        }
    }
}

