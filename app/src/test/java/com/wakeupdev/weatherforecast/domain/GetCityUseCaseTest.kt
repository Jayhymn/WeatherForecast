package com.wakeupdev.weatherforecast.domain

import com.wakeupdev.weatherforecast.data.api.City
import com.wakeupdev.weatherforecast.data.repos.CityRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class GetCityUseCaseTest {

    // Mock the CityRepository
    private val cityRepository: CityRepository = mockk()

    // Create an instance of GetCityUseCase, passing the mock repository
    private val getCityUseCase = GetCityUseCase(cityRepository)

    @Test
    fun `test searchCity success`() = runBlocking {
        val searchQuery = "New York"

        // Mock the repository's searchCity function
        val mockCities = listOf(
            City(
                id = 1,
                name = "New York",
                state = "NY",
                country = "USA",
                latitude = 40.7128,
                longitude = -74.0060
            ),
            City(
                id = 2,
                name = "Los Angeles",
                state = "CA",
                country = "USA",
                latitude = 34.0522,
                longitude = -118.2437
            )
        )

        coEvery { cityRepository.searchCity(searchQuery) } returns mockCities

        // Call the use case
        val result = getCityUseCase(searchQuery)

        // Verify the result
        assertEquals(mockCities, result)

        // Verify that the repository's searchCity method was called
        coVerify { cityRepository.searchCity(searchQuery) }
    }

    @Test
    fun `test searchCity empty result`() = runBlocking {
        val searchQuery = "NonExistentCity"

        // Mock the repository's searchCity function to return an empty list
        coEvery { cityRepository.searchCity(searchQuery) } returns emptyList()

        // Call the use case
        val result = getCityUseCase(searchQuery)

        // Verify the result
        assertTrue(result.isEmpty())

        // Verify that the repository's searchCity method was called
        coVerify { cityRepository.searchCity(searchQuery) }
    }

    @Test
    fun `test searchCity error handling`() = runBlocking {
        val searchQuery = "New York"

        // Mock the repository's searchCity function to throw an exception
        coEvery { cityRepository.searchCity(searchQuery) } throws Exception("Network error")

        try {
            // Call the use case and expect it to throw an exception
            getCityUseCase(searchQuery)
            fail("Expected exception to be thrown")
        } catch (e: Exception) {
            // Verify that the exception is thrown
            assertEquals("Network error", e.message)
        }

        // Verify that the repository's searchCity method was called
        coVerify { cityRepository.searchCity(searchQuery) }
    }
}
