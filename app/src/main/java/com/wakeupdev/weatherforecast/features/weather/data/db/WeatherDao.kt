package com.wakeupdev.weatherforecast.features.weather.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WeatherDao {
    @Query("SELECT * FROM weather WHERE cityName = :cityName LIMIT 1")
    suspend fun getWeatherData(cityName: String): WeatherEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherData(weatherEntity: WeatherEntity)

//    @Update
//    suspend fun updateWeatherData(id: Long, weatherEntity: WeatherEntity)

    @Query("UPDATE weather SET currentTemperature = :currentTemperature, weatherCondition = :weatherCondition, " +
            "dailyForecast = :dailyForecast WHERE cityName = :cityName")
    suspend fun updateWeatherData(
        cityName: String,
        currentTemperature: Double,
        weatherCondition: String,
        dailyForecast: String
    ) : Unit
}