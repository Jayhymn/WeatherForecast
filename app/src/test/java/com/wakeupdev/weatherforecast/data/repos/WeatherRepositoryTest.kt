package com.wakeupdev.weatherforecast.data.repos

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.wakeupdev.weatherforecast.data.DailyForecast
import com.wakeupdev.weatherforecast.data.HourlyTemperature
import com.wakeupdev.weatherforecast.data.WeatherData
import com.wakeupdev.weatherforecast.data.api.WeatherApiService
import com.wakeupdev.weatherforecast.data.api.WeatherResponse
import com.wakeupdev.weatherforecast.data.db.dao.CityDao
import com.wakeupdev.weatherforecast.data.db.dao.WeatherDao
import com.wakeupdev.weatherforecast.data.db.entities.CityEntity
import com.wakeupdev.weatherforecast.data.db.entities.WeatherEntity
import com.wakeupdev.weatherforecast.data.db.entities.toWeatherData
import com.wakeupdev.weatherforecast.data.toEntity
import com.wakeupdev.weatherforecast.utils.Logger
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class WeatherRepositoryTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()  // Ensures LiveData updates are executed synchronously

    private lateinit var weatherRepository: WeatherRepository
    private val apiService: WeatherApiService = mockk()
    private val weatherDao: WeatherDao = mockk()
    private val cityDao: CityDao = mockk()
    private val mockLogger: Logger = mockk(relaxed = true)

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        weatherRepository = WeatherRepository(apiService, weatherDao, cityDao, mockLogger)
    }

    @Test
    fun `test getWeatherFromApi success`() = runTest {
        val lat = 40.7128
        val lon = -74.0060
        val weatherResponse = mockk<WeatherResponse>()

        coEvery { apiService.getWeather(lat, lon) } returns weatherResponse
        coEvery { weatherDao.getWeatherData(lat, lon) } returns null // Simulate no cached data

        val result = weatherRepository.getWeatherFromApi(lat, lon)

        assertEquals(weatherResponse, result)
        coVerify { apiService.getWeather(lat, lon) }
        coVerify { weatherDao.getWeatherData(lat, lon) }
    }

    @Test
    fun `test getWeatherFromApi with cached data`() = runTest {
        val lat = 40.7128
        val lon = -74.0060
        val cachedWeatherEntity = mockk<WeatherEntity>()
        val cachedWeatherData = mockk<WeatherData>()

        coEvery { apiService.getWeather(lat, lon) } throws Exception("API failure")
        coEvery { weatherDao.getWeatherData(lat, lon) } returns cachedWeatherEntity
        coEvery { cachedWeatherEntity.toWeatherData() } returns cachedWeatherData

        val result = weatherRepository.getWeatherFromApi(lat, lon)

        assertEquals(cachedWeatherData, result)
        coVerify { apiService.getWeather(lat, lon) }
        coVerify { weatherDao.getWeatherData(lat, lon) }
    }

    @Test
    fun `test syncWeatherData`() = runTest {
        val favoriteCities = listOf(
            CityEntity(id = 1L, name = "New York", state = "NY", country = "USA", latitude = 40.7128, longitude = -74.0060),
            CityEntity(id = 2L, name = "London", state = "England", country = "UK", latitude = 51.5074, longitude = -0.1278)
        )

        val weatherData = mockk<WeatherData>()
        val weatherEntity = mockk<WeatherEntity>()

        coEvery { cityDao.getFavoriteCities() } returns favoriteCities
        coEvery { apiService.getWeather(any(), any()) } returns mockk<WeatherResponse>()
        coEvery { weatherData.toEntity() } returns weatherEntity
        coEvery { weatherDao.updateWeatherData(weatherEntity) } just Runs

        weatherRepository.syncWeatherData()

        coVerify(exactly = 2) { apiService.getWeather(any(), any()) }
        coVerify(exactly = 2) { weatherDao.updateWeatherData(weatherEntity) }
    }


    @Test
    fun `test cacheWeatherData insert new data`() = runTest {
        val weatherData = mockk<WeatherData>()
        val cityName = "New York"
        val currentTemperature = 25.0
        val minTemperature = 18.0 // Mock value for minTemperature
        val maxTemperature = 30.0 // Mock value for maxTemperature
        val weatherCondition = "Clear"
        val weatherIcon = "clear_icon"
        val latitude = 40.7128
        val longitude = -74.0060
        val windSpeed = 10.0
        val humidity = 60.0
        val uvIndex = 5.0
        val pressure = 1013.0
        val visibility = 10.0
        val dewPoint = 15.0
        val hourlyTemperature = mockk<List<HourlyTemperature>>()
        val dailyForecast = mockk<List<DailyForecast>>()
        val isCurrentCity = true
        val weatherEntity = mockk<WeatherEntity>()

        // Mocking all required methods
        coEvery { weatherData.cityName } returns cityName
        coEvery { weatherData.currentTemperature } returns currentTemperature
        coEvery { weatherData.weatherCondition } returns weatherCondition
        coEvery { weatherData.minTemperature } returns minTemperature // Mock the getMinTemperature() method
        coEvery { weatherData.maxTemperature } returns maxTemperature
        coEvery { weatherData.weatherIcon } returns weatherIcon
        coEvery { weatherData.latitude } returns latitude
        coEvery { weatherData.longitude } returns longitude
        coEvery { weatherData.windSpeed } returns windSpeed
        coEvery { weatherData.humidity } returns humidity
        coEvery { weatherData.uvIndex } returns uvIndex
        coEvery { weatherData.pressure } returns pressure
        coEvery { weatherData.visibility } returns visibility
        coEvery { weatherData.dewPoint } returns dewPoint
        coEvery { weatherData.hourlyTemperature } returns hourlyTemperature
        coEvery { weatherData.dailyForecast } returns dailyForecast

        // Mock the iterators for Gson serialization
        coEvery { hourlyTemperature.iterator() } returns mockk()
        coEvery { dailyForecast.iterator() } returns mockk()

        coEvery { weatherDao.getWeatherDataForCurrentLocation() } returns null
        coEvery { weatherDao.getWeatherDataByCityName(cityName) } returns null
        coEvery { weatherDao.insertWeatherData(any()) } returns Unit
        coEvery { weatherData.toEntity() } returns weatherEntity

        weatherRepository.cacheWeatherData(weatherData, isCurrentCity)

        coVerify { weatherDao.insertWeatherData(weatherEntity) }
    }


    @Test
    fun `test cacheWeatherData update existing data`() = runTest {
        val weatherData = mockk<WeatherData>()
        val cityName = "New York"
        val isCurrentCity = false
        val weatherEntity = mockk<WeatherEntity>()
        val existingWeatherEntity = mockk<WeatherEntity>()

        coEvery { weatherDao.getWeatherDataForCurrentLocation() } returns null
        coEvery { weatherDao.getWeatherDataByCityName(cityName) } returns existingWeatherEntity
        coEvery { weatherDao.updateWeatherData(any()) } just Runs
        coEvery { weatherData.toEntity() } returns weatherEntity

        weatherRepository.cacheWeatherData(weatherData, isCurrentCity)

        coVerify { weatherDao.updateWeatherData(weatherEntity) }
    }

}
