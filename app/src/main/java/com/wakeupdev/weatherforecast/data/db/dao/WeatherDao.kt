package com.wakeupdev.weatherforecast.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.wakeupdev.weatherforecast.data.db.entities.WeatherEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
    @Query("SELECT * FROM weather WHERE currentLocation IS 1 LIMIT 1")
    fun getCurrentWeather(): Flow<WeatherEntity?>

    @Query("SELECT * FROM weather WHERE cityName IS :cityName LIMIT 1")
    fun getFavoriteWeather(cityName: String): Flow<WeatherEntity?>

    @Query("SELECT * FROM weather WHERE latitude = :lat AND longitude =:lon LIMIT 1")
    suspend fun getWeatherData(lat: Double, lon: Double): WeatherEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherData(weatherEntity: WeatherEntity)

    @Upsert
    suspend fun upsertWeatherData(weatherEntity: WeatherEntity)

    @Insert
    suspend fun updateWeatherData(weatherEntity: WeatherEntity)

    @Query("SELECT * FROM weather WHERE currentLocation = 1 LIMIT 1")
    suspend fun getWeatherDataForCurrentLocation(): WeatherEntity?

    @Query("SELECT * FROM weather WHERE cityName = :cityName LIMIT 1")
    suspend fun getWeatherDataByCityName(cityName: String): WeatherEntity?

}