package com.wakeupdev.weatherforecast.data.api.models

import com.wakeupdev.weatherforecast.data.WeatherData

data class WeatherResponse(
    val name: String, // City name
    val lat: Double,
    val lon: Double,
    val weather: List<Weather>,
    val main: Main,
    val wind: Wind
) fun WeatherResponse.toWeatherData(): WeatherData {
    return WeatherData(
        cityName = name,
        currentTemperature = main.temp,
        weatherCondition = weather.firstOrNull()?.description.orEmpty(),
        dailyForecast = listOf() // Populate with 7-day forecast if available
    )
}


data class Weather(
    val description: String,  // Weather conditions (e.g., clear sky)
    val icon: String          // Icon code for weather conditions
)

data class Main(
    val temp: Double,         // Temperature in Kelvin
    val humidity: Int         // Humidity percentage
)

data class Wind(
    val speed: Double         // Wind speed in meters per second
)
