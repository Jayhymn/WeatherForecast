package com.wakeupdev.weatherforecast.data.api.models

data class ForecastResponse(
    val daily: List<DailyForecast>
)

data class DailyForecast(
    val dt: Long,             // Date (timestamp)
    val temp: Temp,           // Temperature (min and max)
    val humidity: Int,        // Humidity percentage
    val weather: List<Weather>,
    val wind_speed: Double    // Wind speed
)

data class Temp(
    val day: Double,          // Day temperature (in Celsius)
    val min: Double,          // Min temperature (in Celsius)
    val max: Double           // Max temperature (in Celsius)
)