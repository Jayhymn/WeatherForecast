package com.wakeupdev.weatherforecast.features.city.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(city: CityEntity)

    @Delete
    suspend fun delete(city: CityEntity)

    @Query("SELECT * FROM cities")
    suspend fun getAllCities(): List<CityEntity>

    @Query("SELECT * FROM cities WHERE name = :cityName LIMIT 1")
    suspend fun getCityByName(cityName: String): CityEntity?
}
