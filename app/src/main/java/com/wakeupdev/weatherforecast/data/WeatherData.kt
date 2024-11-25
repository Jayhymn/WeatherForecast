package com.wakeupdev.weatherforecast.data

data class WeatherData(
    val cityName: String,
    val currentTemperature: Double,
    val weatherCondition: String,
    val dailyForecast: List<DailyForecast>
)
