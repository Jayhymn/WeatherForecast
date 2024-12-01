package com.wakeupdev.weatherforecast.data.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wakeupdev.weatherforecast.data.DailyForecast
import com.wakeupdev.weatherforecast.data.HourlyTemperature
import com.wakeupdev.weatherforecast.data.WeatherData

@Entity(
    tableName = "weather",
//    foreignKeys = [
//        ForeignKey(
//            entity = CityEntity::class,
//            parentColumns = ["id"],
//            childColumns = ["cityId"],
//            onDelete = ForeignKey.CASCADE
//        )
//    ],
//    indices = [Index(value = ["cityName"], unique = true)]
)
data class WeatherEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val currentLocation: Boolean = false,
    val cityName: String,
    val cityId: Long,
    val currentTemperature: Double,
    val weatherCondition: String,
    val weatherIcon: String,
    val dailyForecast: String, // Store as JSON or serialized string
    val hourlyForecast: String, // Store as JSON or serialized string
    val minTemperature: Double,
    val maxTemperature: Double,
    val latitude: Double,
    val windSpeed: Double,
    val humidity: Double,
    val uvIndex: Double,
    val pressure: Double,
    val visibility: Double,
    val dewPoint: Double,
    val longitude: Double
)

fun WeatherEntity.toWeatherData(): WeatherData {
    return WeatherData(
        cityName = this.cityName,
        currentTemperature = this.currentTemperature,
        weatherCondition = this.weatherCondition,
        minTemperature = this.minTemperature,
        maxTemperature = this.maxTemperature,
        weatherIcon = this.weatherIcon,
        latitude = this.latitude,
        longitude = this.longitude,
        windSpeed = this.windSpeed,
        humidity = this.humidity,
        pressure = this.pressure,
        dewPoint = this.dewPoint,
        uvIndex = this.uvIndex,
        visibility = this.visibility,
        hourlyTemperature = Gson().fromJson(this.hourlyForecast, object : TypeToken<List<HourlyTemperature>>() {}.type),
        dailyForecast = Gson().fromJson(this.dailyForecast, object : TypeToken<List<DailyForecast>>() {}.type),
    )
}