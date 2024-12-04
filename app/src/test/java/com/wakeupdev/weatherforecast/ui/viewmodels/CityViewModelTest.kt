package com.wakeupdev.weatherforecast.ui.viewmodels

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.wakeupdev.weatherforecast.data.DailyForecast
import com.wakeupdev.weatherforecast.data.HourlyTemperature
import com.wakeupdev.weatherforecast.data.WeatherData
import com.wakeupdev.weatherforecast.data.api.City
import com.wakeupdev.weatherforecast.data.api.GeocodingApiService
import com.wakeupdev.weatherforecast.data.repos.CityRepository
import com.wakeupdev.weatherforecast.ui.CityUiState
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class CityViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()  // Ensures LiveData updates are executed synchronously

    private lateinit var cityViewModel: CityViewModel
    private val cityRepository: CityRepository = mockk()
    private val geocodingApiService: GeocodingApiService = mockk()

    // Use TestCoroutineDispatcher to control coroutines in tests
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        cityViewModel = CityViewModel(geocodingApiService, cityRepository)
        mockkStatic(Log::class)
        every { Log.e(any(), any(), any()) } returns 0
    }

    @Test
    fun `test searchCity success`() = runTest {
        val searchQuery = "London"
        val cities = listOf(
            City(
                id = 1L,
                name = "Berlin",
                state = "Berlin",
                country = "Germany",
                latitude = 52.52,
                longitude = 13.405)
        )
        coEvery { cityRepository.searchCity(searchQuery) } returns cities

        cityViewModel.searchCity(searchQuery)

        // Observe the StateFlow
        cityViewModel.citiesSearch.collect { state ->
            when (state) {
                is CityUiState.Success -> assertEquals(cities, state.citiesData)
                is CityUiState.Error -> fail("Expected success, but got error")
                is CityUiState.Loading -> fail("Expected success, but got loading")
                is CityUiState.Idle -> fail("Expected success, but got idle")
            }
        }
    }

    @Test
    fun `test searchCity error`() = runTest {
        val searchQuery = "London"
        val exception = Exception("Network error")
        coEvery { cityRepository.searchCity(searchQuery) } throws exception

        cityViewModel.searchCity(searchQuery)

        // Observe the StateFlow
        cityViewModel.citiesSearch.collect { state ->
            when (state) {
                is CityUiState.Error -> assertEquals("Network error", state.message)
                is CityUiState.Success -> fail("Expected error, but got success")
                is CityUiState.Loading -> fail("Expected error, but got loading")
                is CityUiState.Idle -> fail("Expected error, but got idle")
            }
        }
    }

    @Test
    fun `test getFavoriteCities success`() = runTest {
        val cities = listOf(
            City(
                id = 3L,
                name = "New York",
                state = "New York",
                country = "USA",
                latitude = 40.7128,
                longitude = -74.0060
            ),
            City(
                id = 4L,
                name = "Tokyo",
                state = "Tokyo",
                country = "Japan",
                latitude = 35.6762,
                longitude = 139.6503
            )
        )
        coEvery { cityRepository.getFavCities() } returns flow { emit(cities) }

        cityViewModel.getFavoriteCities()

        // Observe the StateFlow
        cityViewModel.favCities.collect { state ->
            when (state) {
                is CityUiState.Success -> assertEquals(cities, state.citiesData)
                is CityUiState.Error -> fail("Expected success, but got error")
                is CityUiState.Loading -> fail("Expected success, but got loading")
                is CityUiState.Idle -> fail("Expected success, but got idle")
            }
        }
    }

    @Test
    fun `test getFavoriteCities error`() = runTest {
        val exception = Exception("Error fetching favorite cities")
        coEvery { cityRepository.getFavCities() } throws exception

        cityViewModel.getFavoriteCities()

        // Observe the StateFlow
        cityViewModel.favCities.collect { state ->
            when (state) {
                is CityUiState.Error -> assertEquals("Error fetching favorite cities", state.message)
                is CityUiState.Success -> fail("Expected error, but got success")
                is CityUiState.Loading -> fail("Expected error, but got loading")
                is CityUiState.Idle -> fail("Expected error, but got idle")
            }
        }
    }

    @Test
    fun `test saveFavoriteCity`() = runTest {
        val city = City(
            id = 6L,
            name = "Sydney",
            state = "New South Wales",
            country = "Australia",
            latitude = -33.8688,
            longitude = 151.2093
        )
        val weatherData =
            WeatherData(
                cityName = "New York",
                currentTemperature = 22.3,
                weatherCondition = "Partly Cloudy",
                minTemperature = 18.0,
                maxTemperature = 25.0,
                weatherIcon = "02d",
                latitude = 40.7128,
                longitude = -74.0060,
                windSpeed = 4.5,
                humidity = 75.0,
                uvIndex = 5.0,
                pressure = 1012.0,
                visibility = 10.0,
                dewPoint = 15.0,
                hourlyTemperature = listOf(
                    HourlyTemperature(date = "2024-12-03 08:00", temperature = 21.0, weatherIcon = "02d"),
                    HourlyTemperature(date = "2024-12-03 09:00", temperature = 22.0, weatherIcon = "02d")
                ),
                dailyForecast = listOf(
                    DailyForecast(date = "2024-12-03", minTemperature = 18.0, maxTemperature = 25.0, condition = "Partly Cloudy", weatherIcon = "02d", humidity = 75.0),
                    DailyForecast(date = "2024-12-04", minTemperature = 20.0, maxTemperature = 26.0, condition = "Cloudy", weatherIcon = "03d", humidity = 70.0)
                )
            )

        coEvery { cityRepository.saveFavCity(city, weatherData) } returns 1L

        val result = cityViewModel.saveFavoriteCity(city, weatherData)

        assertEquals(1L, result)
        coVerify { cityRepository.saveFavCity(city, weatherData) }
    }

    @Test
    fun `test clearSearchCitiesData`() = runTest {
        cityViewModel.clearSearchCitiesData()

        cityViewModel.citiesSearch.collect { state ->
            when (state) {
                is CityUiState.Success -> assertTrue(state.citiesData.isEmpty())
                else -> fail("Expected Success state with empty list, but got ${state::class}")
            }
        }
    }

    @Test
    fun `test deleteCities`() = runTest {
        val cities = listOf(City(
            id = 4L,
            name = "Tokyo",
            state = "Tokyo",
            country = "Japan",
            latitude = 35.6762,
            longitude = 139.6503
        )
        )
        coEvery { cityRepository.deleteCities(cities) } just Runs

        cityViewModel.deleteCities(cities)

        coVerify { cityRepository.deleteCities(cities) }
    }
}
