package com.wakeupdev.weatherforecast.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.wakeupdev.weatherforecast.data.db.WeatherEntity

@Dao
interface WeatherDao {
    @Query("SELECT * FROM weather WHERE latitude = :lat AND longitude =:lon LIMIT 1")
    suspend fun getWeatherData(lat: Double, lon: Double): WeatherEntity?

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