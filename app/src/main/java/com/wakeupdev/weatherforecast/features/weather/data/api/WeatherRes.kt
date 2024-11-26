package com.wakeupdev.weatherforecast.features.weather.data.api

import com.google.gson.Gson
import com.wakeupdev.weatherforecast.features.weather.data.DailyForecast
import com.wakeupdev.weatherforecast.features.weather.data.WeatherData
import com.wakeupdev.weatherforecast.features.weather.data.db.WeatherEntity
import com.wakeupdev.weatherforecast.shared.domain.FormatDateUseCase
import javax.inject.Inject

data class WeatherResponse(
    val alerts: List<Alert?>,
    val current: Current,
    val daily: List<Daily?>,
    val hourly: List<Hourly?>,
    val lat: Double,
    val lon: Double,
    val minutely: List<Minutely?>,
    val timezone: String,
    val timezone_offset: Int
)


fun WeatherResponse.toWeatherData(): WeatherData {
    return WeatherData(
        cityName = this.timezone,
        currentTemperature = this.current.temp,
        weatherCondition = this.current.weather.getOrNull(0)?.main ?: "Unknown",
        weatherIcon = this.current.weather.getOrNull(0)?.icon ?: "",
        maxTemperature = this.daily.getOrNull(0)?.temp?.max ?: 0.0,
        minTemperature = this.daily.getOrNull(0)?.temp?.min ?: 0.0,
        dailyForecast = this.daily.mapNotNull { it?.toDailyForecast() }
    )
}

fun WeatherResponse.toWeatherEntity(): WeatherEntity {
    return WeatherEntity(
        cityName = this.timezone,
        currentTemperature = this.current.temp,
        weatherCondition = this.current.weather.getOrNull(0)?.main ?: "Unknown", // Safe call and fallback value
        weatherIcon = this.current.weather.getOrNull(0)?.icon ?: "", // Safe call and fallback value
        dailyForecast = Gson().toJson(this.daily.mapNotNull { it?.toDailyForecast() }),
        maxTemperature = this.daily.getOrNull(0)?.temp?.max ?: 0.0,
        minTemperature = this.daily.getOrNull(0)?.temp?.min ?: 0.0
    )
}


data class Alert(
    val description: String,
    val end: Int,
    val event: String,
    val sender_name: String,
    val start: Int,
    val tags: List<Any>
)

data class Current(
    val clouds: Int,
    val dew_point: Double,
    val dt: Int,
    val feels_like: Double,
    val humidity: Double,
    val pressure: Double,
    val sunrise: Int,
    val sunset: Int,
    val temp: Double,
    val uvi: Double,
    val visibility: Int,
    val weather: List<Weather>,
    val wind_deg: Int,
    val wind_gust: Double,
    val wind_speed: Double
)

data class Daily(
    val clouds: Int,
    val dew_point: Double,
    val dt: Int,
    val feels_like: FeelsLike,
    val humidity: Double,
    val moon_phase: Double,
    val moonrise: Int,
    val moonset: Int,
    val pop: Double,
    val pressure: Int,
    val rain: Double,
    val summary: String,
    val sunrise: Int,
    val sunset: Int,
    val temp: Temp,
    val uvi: Double,
    val weather: List<Weather>,
    val wind_deg: Int,
    val wind_gust: Double,
    val wind_speed: Double
) {
    @Inject
    lateinit var formatDateUseCase: FormatDateUseCase

    private val formattedDate = formatDateUseCase(dt.toLong())

    fun toDailyForecast() : DailyForecast {
        return DailyForecast(
            date = formattedDate,
            minTemperature = temp.min,
            maxTemperature = temp.max,
            condition = summary,
            humidity = humidity,
            weatherIcon = weather.getOrNull(0)?.icon ?: ""
        )
    }
}

data class Hourly(
    val clouds: Int,
    val dew_point: Double,
    val dt: Int,
    val feels_like: Double,
    val humidity: Int,
    val pop: Double,
    val pressure: Int,
    val temp: Double,
    val uvi: Int,
    val visibility: Int,
    val weather: List<Weather>,
    val wind_deg: Int,
    val wind_gust: Double,
    val wind_speed: Double
)

data class Minutely(
    val dt: Int,
    val precipitation: Int
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