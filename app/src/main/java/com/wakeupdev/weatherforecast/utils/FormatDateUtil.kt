package com.wakeupdev.weatherforecast.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FormatDateUtil {

    private val dateFormat: SimpleDateFormat
        get() = SimpleDateFormat("EEE MMM dd", Locale.getDefault())

    private val timeFormat: SimpleDateFormat
        get() = SimpleDateFormat("h a", Locale.getDefault())

    // Function to convert Unix time (UTC) to formatted date (only the date part)
    fun formatToDate(unixTimestamp: Long?): String {
        return try {
            // Convert Unix timestamp (seconds) to Date object
            val date = Date(unixTimestamp?.times(1000L) ?: 0L)  // Convert seconds to milliseconds
            dateFormat.format(date)
        } catch (e: Exception) {
            // Handle any errors and return an empty string in case of an exception
            ""
        }
    }

    fun formatToTime(unixTimestamp: Long?): String {
        return try {
            // Convert Unix timestamp (seconds) to Date object
            val date = Date(unixTimestamp?.times(1000L) ?: 0L)  // Convert seconds to milliseconds
            timeFormat.format(date)
        } catch (e: Exception) {
            // Handle any errors and return an empty string in case of an exception
            ""
        }
    }
}