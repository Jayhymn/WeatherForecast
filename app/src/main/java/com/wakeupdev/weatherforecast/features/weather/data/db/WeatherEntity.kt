package com.wakeupdev.weatherforecast.features.weather.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wakeupdev.weatherforecast.features.weather.data.DailyForecast
import com.wakeupdev.weatherforecast.features.weather.data.WeatherData
import com.wakeupdev.weatherforecast.features.weather.data.api.toWeatherData

@Entity(tableName = "weather")
data class WeatherEntity(
    @PrimaryKey val cityName: String,
    val currentTemperature: Double,
    val weatherCondition: String,
    val weatherIcon: String,
    val dailyForecast: String, // Store as JSON or serialized string
    val minTemperature: Double,
    val maxTemperature: Double,
    val latitude: Double,
    val longitude: Double
)

fun WeatherEntity.toDomainModel(): WeatherData {
    return WeatherData(
        cityName = this.cityName,
        currentTemperature = this.currentTemperature,
        weatherCondition = this.weatherCondition,
        minTemperature = this.minTemperature,
        maxTemperature = this.maxTemperature,
        weatherIcon = this.weatherIcon,
        latitude = this.latitude,
        longitude = this.longitude,
        dailyForecast = Gson().fromJson(this.dailyForecast, object : TypeToken<List<DailyForecast>>() {}.type),
    )
}