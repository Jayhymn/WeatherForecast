package com.wakeupdev.weatherforecast.data

data class DailyForecast(
    val date: String,
    val minTemperature: Double,
    val maxTemperature: Double,
    val condition: String
)