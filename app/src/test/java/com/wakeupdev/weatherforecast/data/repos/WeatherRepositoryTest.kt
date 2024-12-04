package com.wakeupdev.weatherforecast.data.repos

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.wakeupdev.weatherforecast.data.DailyForecast
import com.wakeupdev.weatherforecast.data.HourlyTemperature
import com.wakeupdev.weatherforecast.data.WeatherData
import com.wakeupdev.weatherforecast.data.api.Current
import com.wakeupdev.weatherforecast.data.api.Daily
import com.wakeupdev.weatherforecast.data.api.Hourly
import com.wakeupdev.weatherforecast.data.api.Minutely
import com.wakeupdev.weatherforecast.data.api.Temp
import com.wakeupdev.weatherforecast.data.api.Weather
import com.wakeupdev.weatherforecast.data.api.WeatherApiService
import com.wakeupdev.weatherforecast.data.api.WeatherResponse
import com.wakeupdev.weatherforecast.data.api.toWeatherData
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
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame
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
        val lat = 25.2164
        val lon = 55.1654

        // Create mock data for WeatherResponse
        val mockWeatherResponse = mockk<WeatherResponse>().apply {
            coEvery { timezone } returns "Africa/Lagos"
        }

        val mockDailyList = listOf(mockk<Daily>(relaxed = true))
        val mockHourlyList = listOf(mockk<Hourly>(relaxed = true))
        val mockMinutelyList = listOf(mockk<Minutely>(relaxed = true))

        every { mockWeatherResponse.daily } returns mockDailyList
        every { mockWeatherResponse.hourly } returns mockHourlyList
        every { mockWeatherResponse.minutely } returns mockMinutelyList

        val mockCurrent = mockk<Current>(relaxed = true).apply {
            every { temp } returns 25.0
        }

        // Mock the weather list with at least one item
        val mockWeatherList = listOf(mockk<Weather>(relaxed = true))

        val mockWeatherData = mockk<WeatherData>()

        coEvery { apiService.getWeather(eq(lat), eq(lon)) } returns mockWeatherResponse
        coEvery { mockWeatherResponse.toWeatherData() } returns mockWeatherData
        coEvery { mockWeatherResponse.current } returns mockCurrent
        coEvery { weatherDao.getWeatherData(eq(lat), eq(lon)) } returns null
        coEvery { mockCurrent.weather } returns mockWeatherList

        // Call the function
        val result = weatherRepository.getWeatherFromApi(lat, lon)

        // Assert correct result and interactions
        assertSame(mockWeatherData, result)
        coVerify { apiService.getWeather(eq(lat), eq(lon)) }
        coVerify(exactly = 0) { weatherDao.getWeatherData(eq(lat), eq(lon)) }
    }

    @Test
    fun `test getWeatherFromApi with cached data`() = runTest {
        val lat = 40.7128
        val lon = -74.0060

        // Create mock data for the cached weather entity and conversion
        val mockCachedWeatherEntity = mockk<WeatherEntity>()
        val mockCachedWeatherData = mockk<WeatherData>()

        coEvery { weatherDao.getWeatherData(lat, lon) } returns mockCachedWeatherEntity
        coEvery { mockCachedWeatherEntity.toWeatherData() } returns mockCachedWeatherData
        coEvery { apiService.getWeather(lat, lon) } throws Exception("API failure")

        val result = weatherRepository.getWeatherFromApi(lat, lon)

        // Assert the cached data is returned when API fails
        assertEquals(mockCachedWeatherData, result)
        coVerify { weatherDao.getWeatherData(lat, lon) }
        coVerify(exactly = 1) { apiService.getWeather(lat, lon) }
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
