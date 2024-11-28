package com.wakeupdev.weatherforecast.data.api

import com.google.gson.Gson
import com.wakeupdev.weatherforecast.data.db.DailyForecast
import com.wakeupdev.weatherforecast.data.db.HourlyTemperature
import com.wakeupdev.weatherforecast.data.db.WeatherData
import com.wakeupdev.weatherforecast.data.db.WeatherEntity
import com.wakeupdev.weatherforecast.utils.FormatDateUtil

data class WeatherResponse(
    val alerts: List<Alert?>,
    val current: Current,
    val daily: List<Daily?>,
    val hourly: List<Hourly?>,
    val lat: Double,
    val lon: Double,
    val minutely: List<Minutely?>,
    val timezone: String,
    val timezone_offset: Double
)


fun WeatherResponse.toWeatherData(): WeatherData {
    return WeatherData(
        cityName = this.timezone,
        currentTemperature = this.current.temp,
        weatherCondition = this.current.weather.getOrNull(0)?.main ?: "Unknown",
        weatherIcon = this.current.weather.getOrNull(0)?.icon ?: "",
        maxTemperature = this.daily.getOrNull(0)?.temp?.max ?: 0.0,
        minTemperature = this.daily.getOrNull(0)?.temp?.min ?: 0.0,
        latitude = this.lat,
        longitude = this.lon,
        windSpeed = this.current.wind_speed ?: 0.0,
        humidity = this.current.humidity ?: 0.0,
        pressure = this.current.pressure ?: 0.0,
        dewPoint = this.current.dew_point ?: 0.0,
        uvIndex = this.current.uvi ?: 0.0,
        visibility = this.current.visibility ?: 0.0,
        hourlyTemperature = this.hourly.mapNotNull { it?.toHourlyTemperature() },
        dailyForecast = this.daily.mapNotNull { it?.toDailyForecast() }
    )
}

fun WeatherResponse.toWeatherEntity(): WeatherEntity {
    return WeatherEntity(
        cityName = this.timezone,
        currentTemperature = this.current.temp ?: 0.0,
        weatherCondition = this.current.weather.getOrNull(0)?.main ?: "Unknown", // Safe call and fallback value
        weatherIcon = this.current.weather.getOrNull(0)?.icon ?: "", // Safe call and fallback value
        dailyForecast = Gson().toJson(this.daily.mapNotNull { it?.toDailyForecast() }),
        maxTemperature = this.daily.getOrNull(0)?.temp?.max ?: 0.0,
        latitude = this.lat,
        longitude = this.lon,
        windSpeed = this.current.wind_speed ?: 0.0,
        humidity = this.current.humidity ?: 0.0,
        pressure = this.current.pressure ?: 0.0,
        dewPoint = this.current.dew_point ?: 0.0,
        uvIndex = this.current.uvi ?: 0.0,
        visibility = this.current.visibility ?: 0.0,
        hourlyForecast = Gson().toJson(this.hourly.mapNotNull { it?.toHourlyTemperature() }),
        minTemperature = this.daily.getOrNull(0)?.temp?.min ?: 0.0
    )
}


data class Alert(
    val description: String?,
    val end: Double?,
    val event: String?,
    val sender_name: String?,
    val start: Double?,
    val tags: List<Any>
)

data class Current(
    val clouds: Double?,
    val dew_point: Double?,
    val dt: Double?,
    val feels_like: Double?,
    val humidity: Double?,
    val pressure: Double?,
    val sunrise: Double?,
    val sunset: Double?,
    val temp: Double?,
    val uvi: Double?,
    val visibility: Double?,
    val weather: List<Weather>,
    val wind_deg: Double?,
    val wind_gust: Double?,
    val wind_speed: Double?
)

data class Daily(
    val clouds: Double?,
    val dew_point: Double?,
    val dt: Long?,
    val feels_like: FeelsLike,
    val humidity: Double?,
    val moon_phase: Double?,
    val moonrise: Long?,
    val moonset: Long?,
    val pop: Double?,
    val pressure: Double?,
    val rain: Double?,
    val summary: String,
    val sunrise: Long?,
    val sunset: Long?,
    val temp: Temp,
    val uvi: Double?,
    val weather: List<Weather>,
    val wind_deg: Double?,
    val wind_gust: Double?,
    val wind_speed: Double?,
) {

    fun toDailyForecast() : DailyForecast {
        return DailyForecast(
            date = FormatDateUtil.formatToDate(dt),
            minTemperature = temp.min,
            maxTemperature = temp.max,
            condition = summary,
            humidity = humidity,
            weatherIcon = weather.getOrNull(0)?.icon ?: ""
        )
    }
}

data class Hourly(
    val clouds: Double,
    val dew_point: Double,
    val dt: Long,
    val feels_like: Double,
    val humidity: Double,
    val pop: Double,
    val pressure: Double,
    val temp: Double,
    val uvi: Double,
    val visibility: Double,
    val weather: List<Weather>,
    val wind_deg: Double,
    val wind_gust: Double,
    val wind_speed: Double
){
    fun toHourlyTemperature() : HourlyTemperature {
        return HourlyTemperature(
            date = FormatDateUtil.formatToTime(dt),
            temperature = temp,
            weatherIcon = weather[0].icon
        )
    }
}

data class Minutely(
    val dt: Double,
    val precipitation: Double
)

data class Weather(
    val description: String,
    val icon: String,
    val id: Int,
    val main: String
)

data class FeelsLike(
    val day: Double,
    val eve: Double,
    val morn: Double,
    val night: Double
)

data class Temp(
    val day: Double,
    val eve: Double,
    val max: Double,
    val min: Double,
    val morn: Double,
    val night: Double
)