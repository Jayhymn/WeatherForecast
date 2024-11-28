package com.wakeupdev.weatherforecast.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.wakeupdev.weatherforecast.data.db.entities.CityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(city: CityEntity): Long

    @Delete
    suspend fun delete(city: CityEntity)

    @Query("SELECT * FROM cities")
    fun streamFavoriteCities(): Flow<List<CityEntity>>

    @Query("SELECT * FROM cities")
    suspend fun getFavoriteCities(): List<CityEntity>

    @Query("SELECT * FROM cities WHERE name = :cityName LIMIT 1")
    suspend fun getCityByName(cityName: String): CityEntity?
}
