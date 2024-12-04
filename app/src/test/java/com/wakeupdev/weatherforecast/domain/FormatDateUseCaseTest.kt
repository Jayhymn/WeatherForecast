package com.wakeupdev.weatherforecast.domain

import com.wakeupdev.weatherforecast.utils.Logger
import com.wakeupdev.weatherforecast.utils.MockLogger
import org.junit.Assert.*
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

class FormatDateUseCaseTest {

    private val mockLogger = MockLogger()
    private val formatDateUseCase = FormatDateUseCase(mockLogger)

    @Test
    fun `test valid timestamp`() {
        // Unix timestamp for '2023-12-01'
        val validTimestamp: Long = 1672444800L

        val result = formatDateUseCase(validTimestamp)

        // Check that the result is correctly formatted
        val expectedDate = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(validTimestamp * 1000L))
        assertEquals(expectedDate, result)

        // Print log messages for debugging
        println("Log messages: ${mockLogger.logMessages}")

        // Check if the log message was generated for valid timestamp
        assertTrue(mockLogger.logMessages.contains("Converting timestamp $validTimestamp to date format"))
    }

    @Test
    fun `test null timestamp`() {
        val result = formatDateUseCase(null)

        // Check that the result is "Invalid Date" for null timestamp
        assertEquals("Invalid Date", result)

        // Check if the log message was generated for invalid timestamp
        assertTrue(mockLogger.logMessages.contains("Invalid or null timestamp: null"))
    }

    @Test
    fun `test invalid timestamp`() {
        val invalidTimestamp: Long = -1L

        val result = formatDateUseCase(invalidTimestamp)

        // Check that the result is "Invalid Date" for invalid timestamp
        assertEquals("Invalid Date", result)

        // Check if the log message was generated for invalid timestamp
        assertTrue(mockLogger.logMessages.contains("Invalid or null timestamp: $invalidTimestamp"))
    }

    @Test
    fun `test zero timestamp`() {
        val zeroTimestamp: Long = 0L

        val result = formatDateUseCase(zeroTimestamp)

        // Check that the result is "Invalid Date" for zero timestamp
        assertEquals("Invalid Date", result)

        // Check if the log message was generated for zero timestamp
        assertTrue(mockLogger.logMessages.contains("Invalid or null timestamp: $zeroTimestamp"))
    }

    @Test
    fun `test boundary timestamp`() {
        val boundaryTimestamp: Long = Long.MAX_VALUE // Max value for timestamp

        val result = formatDateUseCase(boundaryTimestamp)

        // Check that the result is correctly formatted
        val expectedDate = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(boundaryTimestamp * 1000L))
        assertEquals(expectedDate, result)

        // Check if the log message was generated for boundary timestamp
        assertTrue(mockLogger.logMessages.contains("Converting timestamp $boundaryTimestamp to date format"))
    }
}
