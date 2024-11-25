package com.wakeupdev.weatherforecast.data.db.weather

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wakeupdev.weatherforecast.data.WeatherData
import com.wakeupdev.weatherforecast.data.api.models.DailyForecast

@Entity(tableName = "weather")
data class WeatherEntity(
    @PrimaryKey val cityName: String,
    val currentTemperature: Double,
    val weatherCondition: String,
    val dailyForecast: String // Store as JSON or serialized string
)

fun WeatherEntity.toDomainModel(): WeatherData {
    return WeatherData(
        cityName = this.cityName,
        currentTemperature = this.currentTemperature,
        weatherCondition = this.weatherCondition,
        dailyForecast = Gson().fromJson(this.dailyForecast, object : TypeToken<List<DailyForecast>>() {}.type)
    )
}

fun WeatherData.toEntity(): WeatherEntity {
    return WeatherEntity(
        cityName = this.cityName,
        currentTemperature = this.currentTemperature,
        weatherCondition = this.weatherCondition,
        dailyForecast = Gson().toJson(this.dailyForecast)
    )
}