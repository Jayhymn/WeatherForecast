package com.wakeupdev.weatherforecast.domain

import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class FormatDateUseCase @Inject constructor() {

    private val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    // Function to convert Unix time (UTC) to formatted date (only the date part)
    operator fun invoke(unixTimestamp: Long?): String {
        return try {
            // Check for null or invalid timestamp
            if (unixTimestamp == null || unixTimestamp <= 0) {
                Log.e("FormatDateUseCase", "Invalid or null timestamp: $unixTimestamp")
                return "Invalid Date"
            }

            // Convert Unix timestamp (seconds) to Date object
            Log.d("FormatDateUseCase", "Converting timestamp $unixTimestamp to date format")

            val date = Date(unixTimestamp * 1000L)  // Convert seconds to milliseconds
            val formattedDate = outputFormat.format(date)

            Log.d("FormatDateUseCase", "Formatted Date: $formattedDate")  // Log the result
            formattedDate
        } catch (e: Exception) {
            // Handle any errors and log them
            Log.e("FormatDateUseCase", "Error converting timestamp: ${e.message}", e)
            "Invalid Date"
        }
    }
}
