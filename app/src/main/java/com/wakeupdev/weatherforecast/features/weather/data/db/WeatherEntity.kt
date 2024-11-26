package com.wakeupdev.weatherforecast.features.weather.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wakeupdev.weatherforecast.features.weather.data.DailyForecast
import com.wakeupdev.weatherforecast.features.weather.data.WeatherData

@Entity(tableName = "weather")
data class WeatherEntity(
    @PrimaryKey val cityName: String,
    val currentTemperature: Double,
    val weatherCondition: String,
    val weatherIcon: String,
    val dailyForecast: String, // Store as JSON or serialized string
    val minTemperature: Double,
    val maxTemperature: Double,
)

fun WeatherEntity.toDomainModel(): WeatherData {
    return WeatherData(
        cityName = this.cityName,
        currentTemperature = this.currentTemperature,
        weatherCondition = this.weatherCondition,
        minTemperature = this.minTemperature,
        maxTemperature = this.maxTemperature,
        weatherIcon = this.weatherIcon,
        dailyForecast = Gson().fromJson(this.dailyForecast, object : TypeToken<List<DailyForecast>>() {}.type),
    )
}