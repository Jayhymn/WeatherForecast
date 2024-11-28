package com.wakeupdev.weatherforecast.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.wakeupdev.weatherforecast.data.db.dao.CityDao
import com.wakeupdev.weatherforecast.data.db.entities.CityEntity
import com.wakeupdev.weatherforecast.data.db.dao.WeatherDao
import com.wakeupdev.weatherforecast.data.db.entities.WeatherEntity

@Database(entities = [CityEntity::class, WeatherEntity::class], version = 1, exportSchema = false)
abstract class LocalDatabase : RoomDatabase() {

    abstract fun cityDao(): CityDao
    abstract fun weatherDao(): WeatherDao
}

