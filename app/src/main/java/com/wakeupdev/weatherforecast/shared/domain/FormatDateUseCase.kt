package com.wakeupdev.weatherforecast.shared.domain

import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class FormatDateUseCase @Inject constructor() {

    private val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    // Function to convert Unix time (UTC) to formatted date (only the date part)
    operator fun invoke(unixTimestamp: Long): String {
        return try {
            // Convert Unix timestamp (seconds) to Date object
            val date = Date(unixTimestamp * 1000)  // Convert seconds to milliseconds
            outputFormat.format(date)
        } catch (e: Exception) {
            // Handle any errors and log them
            Log.e("FormatDateUseCase", e.message, e)
            ""
        }
    }
}
