package com.wakeupdev.weatherforecast.data.repos

import com.wakeupdev.weatherforecast.data.DailyForecast
import com.wakeupdev.weatherforecast.data.HourlyTemperature
import com.wakeupdev.weatherforecast.data.WeatherData
import com.wakeupdev.weatherforecast.data.api.City
import com.wakeupdev.weatherforecast.data.api.GeocodeRes
import com.wakeupdev.weatherforecast.data.api.GeocodeResItem
import com.wakeupdev.weatherforecast.data.api.GeocodingApiService
import com.wakeupdev.weatherforecast.data.api.toCityData
import com.wakeupdev.weatherforecast.data.api.toCityEntity
import com.wakeupdev.weatherforecast.data.db.dao.CityDao
import com.wakeupdev.weatherforecast.data.db.dao.WeatherDao
import com.wakeupdev.weatherforecast.data.toEntity
import com.wakeupdev.weatherforecast.utils.Logger
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class CityRepositoryTest {

    private lateinit var cityRepository: CityRepository
    private val apiService: GeocodingApiService = mockk()
    private val cityDao: CityDao = mockk()
    private val weatherDao: WeatherDao = mockk()
    private val mockLogger: Logger = mockk(relaxed = true)

    private val sampleCity = City(
        id = 10L,
        name = "Cape Town",
        state = "Western Cape",
        country = "South Africa",
        latitude = -33.9249,
        longitude = 18.4241
    )
    private val sampleWeatherData = WeatherData(
        cityName = "Tokyo",
        currentTemperature = 10.0,
        weatherCondition = "Rain",
        minTemperature = 8.0,
        maxTemperature = 12.0,
        weatherIcon = "10d",
        latitude = 35.6762,
        longitude = 139.6503,
        windSpeed = 6.0,
        humidity = 90.0,
        uvIndex = 2.0,
        pressure = 1010.0,
        visibility = 7.0,
        dewPoint = 7.0,
        hourlyTemperature = listOf(
            HourlyTemperature(date = "2024-12-03 08:00", temperature = 9.0, weatherIcon = "10d"),
            HourlyTemperature(date = "2024-12-03 09:00", temperature = 10.0, weatherIcon = "10d")
        ),
        dailyForecast = listOf(
            DailyForecast(date = "2024-12-03", minTemperature = 8.0, maxTemperature = 12.0, condition = "Rain", weatherIcon = "10d", humidity = 90.0),
            DailyForecast(date = "2024-12-04", minTemperature = 9.0, maxTemperature = 14.0, condition = "Showers", weatherIcon = "09d", humidity = 85.0)
        )
    )

    @Before
    fun setUp() {
        cityRepository = CityRepository(apiService, weatherDao, cityDao, mockLogger)
    }

    @Test
    fun `test searchCity success`() = runTest {
        val searchQuery = "New York"

        // Create mock data for GeocodeRes
        val mockGeocodeRes = GeocodeRes().apply {
            add(
                GeocodeResItem(
                    country = "Germany",
                    lat = 52.52,
                    lon = 13.405,
                    name = "Berlin",
                    state = "Berlin"
                )
            )
            add(
                GeocodeResItem(
                    country = "South Africa",
                    lat = -33.9249,
                    lon = 18.4241,
                    name = "Cape Town",
                    state = "Western Cape"
                )
            )
            add(
                GeocodeResItem(
                    country = "Argentina",
                    lat = -34.6037,
                    lon = -58.3816,
                    name = "Buenos Aires",
                    state = "Buenos Aires"
                )
            )
        }

        coEvery { apiService.getGeocodes(searchQuery) } returns mockGeocodeRes

        // Expected result: mapping GeocodeResItem to City
        val expectedCities = mockGeocodeRes.map { it.toCityData() }

        // Call the searchCity function
        val result = cityRepository.searchCity(searchQuery)

        // Assert the result
        assertEquals(expectedCities, result)
        coVerify { apiService.getGeocodes(searchQuery) }
    }


    @Test
    fun `test getFavCities returns flow of cities`() = runTest {
        val mockFavCitiesEntity = listOf(sampleCity.toCityEntity())
        val mockFavCities = listOf(sampleCity)

        coEvery { cityDao.streamFavoriteCities() } returns flowOf(mockFavCitiesEntity)

        val result = cityRepository.getFavCities()

        result.collect { cities ->
            assertEquals(mockFavCities, cities)
        }

        coVerify { cityDao.streamFavoriteCities() }
    }

    @Test
    fun `test saveFavCity success`() = runTest {
        val mockCityEntity = sampleCity.toCityEntity()
        val mockWeatherDataEntity = sampleWeatherData.toEntity()

        // Simulate failure by throwing an exception
        coEvery { cityDao.insert(mockCityEntity) } throws Exception("Insertion failed")
        coEvery { weatherDao.insertWeatherData(mockWeatherDataEntity.copy(id = 0L)) } returns Unit

        val result = cityRepository.saveFavCity(sampleCity, sampleWeatherData)

        // Verify that the error logging was called
        coVerify { mockLogger.e("CityRepository", "Error saving city: ${sampleCity.name}", any()) }

        // Assert the result is 0L because the city insertion failed
        assertEquals(0L, result)
        coVerify(exactly = 0) { weatherDao.insertWeatherData(any()) }
    }

    @Test
    fun `test saveFavCity logs error on failure`() = runTest {
        val mockCityEntity = sampleCity.toCityEntity()
        val mockWeatherDataEntity = sampleWeatherData.toEntity()
        coEvery { cityDao.insert(mockCityEntity) } throws Exception("Insertion failed")

        coEvery { weatherDao.insertWeatherData(mockWeatherDataEntity.copy(id = 0L)) } returns Unit
        val result = cityRepository.saveFavCity(sampleCity, sampleWeatherData)

        coVerify { mockLogger.e("CityRepository", "Error saving city: ${sampleCity.name}", any()) }
        assertEquals(0L, result)

        coVerify(exactly = 0) { weatherDao.insertWeatherData(any()) }
    }

    @Test
    fun `test deleteCities success`() = runTest {
        val citiesToDelete = listOf(sampleCity)

        coEvery { cityDao.deleteCities(citiesToDelete.map { it.id }) } returns Unit

        cityRepository.deleteCities(citiesToDelete)

        coVerify { cityDao.deleteCities(citiesToDelete.map { it.id }) }
    }
}
