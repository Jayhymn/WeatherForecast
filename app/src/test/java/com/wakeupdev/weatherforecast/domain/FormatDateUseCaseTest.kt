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
        val timestamp = 1672444800L
        val formattedDate = formatDateUseCase(timestamp)
        println("Formatted Date: $formattedDate")
        assertEquals("Dec 31, 2022", formattedDate)
    }

    @Test
    fun `test null timestamp`() {
        val formattedDate = formatDateUseCase(null)
        assertEquals("Invalid Date", formattedDate)
    }

    @Test
    fun `test invalid timestamp`() {
        val invalidTimestamp = -1L
        val formattedDate = formatDateUseCase(invalidTimestamp)
        assertEquals("Invalid Date", formattedDate)
    }

}

