package com.wakeupdev.weatherforecast.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.wakeupdev.weatherforecast.data.db.entities.WeatherEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
    @Query("SELECT * FROM weather LIMIT 1")
    fun fetchWeatherData(): Flow<WeatherEntity?>

    @Query("SELECT * FROM weather WHERE latitude = :lat AND longitude =:lon LIMIT 1")
    suspend fun getWeatherData(lat: Double, lon: Double): WeatherEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherData(weatherEntity: WeatherEntity)

    @Update
    suspend fun updateWeatherData(weatherEntity: WeatherEntity)
}