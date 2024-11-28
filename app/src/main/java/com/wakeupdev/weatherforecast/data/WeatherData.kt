package com.wakeupdev.weatherforecast.data

import android.os.Parcelable
import com.google.gson.Gson
import com.wakeupdev.weatherforecast.data.db.entities.WeatherEntity
import kotlinx.parcelize.Parcelize

@Parcelize
data class WeatherData(
    val cityName: String?,
    val currentTemperature: Double?,
    val weatherCondition: String?,
    val minTemperature: Double?,
    val maxTemperature: Double?,
    val weatherIcon: String?,
    val latitude: Double?,
    val longitude: Double?,
    val windSpeed: Double?,
    val humidity: Double?,
    val uvIndex: Double?,
    val pressure: Double?,
    val visibility: Double?,
    val dewPoint: Double?,
    val hourlyTemperature: List<HourlyTemperature>,
    val dailyForecast: List<DailyForecast>,
) : Parcelable

fun WeatherData.toEntity(): WeatherEntity {
    return WeatherEntity(
        cityName = this.cityName ?: "",
        currentTemperature = this.currentTemperature ?: 0.0,
        weatherCondition = this.weatherCondition ?: "",
        minTemperature = this.minTemperature ?: 0.0,
        maxTemperature = this.maxTemperature ?: 0.0,
        weatherIcon = this.weatherIcon ?: "",
        latitude = this.latitude ?: 0.0,
        longitude = this.longitude ?: 0.0,
        windSpeed = this.windSpeed ?: 0.0,
        humidity = this.humidity ?: 0.0,
        pressure = this.pressure ?: 0.0,
        dewPoint = this.dewPoint ?: 0.0,
        uvIndex = this.uvIndex ?: 0.0,
        visibility = this.visibility ?: 0.0,
        hourlyForecast = Gson().toJson(this.hourlyTemperature),
        dailyForecast = Gson().toJson(this.dailyForecast)
    )
}

@Parcelize
data class DailyForecast(
    val date: String?,
    val minTemperature: Double?,
    val maxTemperature: Double,
    val condition: String?,
    val weatherIcon:String?,
    val humidity: Double?
) : Parcelable

@Parcelize
data class HourlyTemperature(
    val date: String?,
    val temperature: Double?,
    val weatherIcon: String?,
) : Parcelable
