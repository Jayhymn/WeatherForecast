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
import com.wakeupdev.weatherforecast.utils.Logger
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
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
    private val mockLogger: Logger = mockk(relaxed = true)

    // Use TestCoroutineDispatcher to control coroutines in tests
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        cityViewModel = CityViewModel(geocodingApiService, cityRepository, mockLogger)
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

        // Launch the view model searchCity method
        cityViewModel.searchCity(searchQuery)

        // Wait until all coroutines are idle
        advanceUntilIdle()

        var state: CityUiState = CityUiState.Idle
        val job = launch {
            cityViewModel.citiesSearch.collect {
                state = it
            }
        }

        // Wait for all coroutines to finish
        advanceUntilIdle()

        // Assert that the state has updated to Success
        assert(state is CityUiState.Success)
        assertEquals(cities, (state as CityUiState.Success).citiesData)

        // Cancel the collection job to clean up
        job.cancel()
    }

    @Test
    fun `test searchCity error`() = runTest {
        val searchQuery = "London"
        val exception = Exception("Network error")
        coEvery { cityRepository.searchCity(searchQuery) } throws exception

        // Launch the view model searchCity method
        cityViewModel.searchCity(searchQuery)

        var state: CityUiState = CityUiState.Idle
        val job = launch {
            cityViewModel.citiesSearch.collect {
                state = it
            }
        }

        // Wait until all coroutines are idle
        advanceUntilIdle()

        // Assert that the state is Error
        assert(state is CityUiState.Error)
        assertEquals("Network error", (state as CityUiState.Error).message)

        // Cancel the collection job to clean up
        job.cancel()
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

        // Launch the view model getFavoriteCities method
        cityViewModel.getFavoriteCities()

        // Wait until all coroutines are idle
        advanceUntilIdle()

        // Collect the state of the favCities flow
        var state: CityUiState = CityUiState.Idle
        val job = launch {
            cityViewModel.favCities.collect {
                state = it
            }
        }

        // Wait until the flow has completed
        advanceUntilIdle()

        // Assert that the state is Success
        assert(state is CityUiState.Success)
        assertEquals(cities, (state as CityUiState.Success).citiesData)

        // Cancel the collection job to clean up
        job.cancel()
    }

    @Test
    fun `test getFavoriteCities error`() = runTest {
        val exception = Exception("Error fetching favorite cities")
        coEvery { cityRepository.getFavCities() } throws exception

        cityViewModel.getFavoriteCities()

        // Use a coroutine to collect the state asynchronously
        var state: CityUiState = CityUiState.Idle // Start with Idle state
        val job = launch {
            cityViewModel.favCities.collect {
                state = it
            }
        }

        advanceUntilIdle()

        assert(state is CityUiState.Error)
        assertEquals("Error fetching favorite cities", (state as CityUiState.Error).message)

        // Clean up by canceling the collection job
        job.cancel()
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

        var state: CityUiState = CityUiState.Idle // Start with Idle state
        val job = launch {
            cityViewModel.citiesSearch.collect {
                state = it // Update the state when the flow emits
            }
        }

        // Allow time for the flow to emit
        advanceUntilIdle()

        if (state is CityUiState.Success) {
            assertTrue((state as CityUiState.Success).citiesData.isEmpty())
        } else {
            fail("Expected Success state with empty list, but got ${state::class}")
        }

        job.cancel()
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
