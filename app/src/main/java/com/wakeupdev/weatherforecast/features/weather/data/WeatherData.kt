package com.wakeupdev.weatherforecast.features.weather.data

import com.google.gson.Gson
import com.wakeupdev.weatherforecast.features.weather.data.db.WeatherEntity

data class WeatherData(
    val cityName: String,
    val currentTemperature: Double,
    val weatherCondition: String,
    val minTemperature: Double,
    val maxTemperature: Double,
    val weatherIcon: String,
    val latitude: Double,
    val longitude: Double,
    val dailyForecast: List<DailyForecast>,
)

fun WeatherData.toEntity(): WeatherEntity {
    return WeatherEntity(
        cityName = this.cityName,
        currentTemperature = this.currentTemperature,
        weatherCondition = this.weatherCondition,
        minTemperature = this.minTemperature,
        maxTemperature = this.maxTemperature,
        weatherIcon = this.weatherIcon,
        latitude = this.latitude,
        longitude = this.longitude,
        dailyForecast = Gson().toJson(this.dailyForecast)
    )
}

data class DailyForecast(
    val date: String,
    val minTemperature: Double,
    val maxTemperature: Double,
    val condition: String,
    val weatherIcon:String,
    val humidity: Double
)
