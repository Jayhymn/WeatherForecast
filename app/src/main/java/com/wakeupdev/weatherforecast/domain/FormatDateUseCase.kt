package com.wakeupdev.weatherforecast.domain

import com.wakeupdev.weatherforecast.utils.Logger
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class FormatDateUseCase @Inject constructor(
    private val logger: Logger
) {
    private val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    operator fun invoke(unixTimestamp: Long?): String {
        return try {
            if (unixTimestamp == null || unixTimestamp <= 0) {
                logger.e("FormatDateUseCase", "Invalid or null timestamp: $unixTimestamp")  // Use logger here
                return "Invalid Date"
            }

            logger.d("FormatDateUseCase", "Converting timestamp $unixTimestamp to date format")  // Use logger here

            val date = Date(unixTimestamp * 1000L)  // Convert seconds to milliseconds
            val formattedDate = outputFormat.format(date)

            logger.d("FormatDateUseCase", "Formatted Date: $formattedDate")  // Log the result using logger
            formattedDate
        } catch (e: Exception) {
            // Handle any errors and log them
            logger.e("FormatDateUseCase", "Error converting timestamp: ${e.message}", e)  // Use logger here
            "Invalid Date"
        }
    }
}
