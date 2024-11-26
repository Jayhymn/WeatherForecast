package com.wakeupdev.weatherforecast.shared

import androidx.room.Database
import androidx.room.RoomDatabase
import com.wakeupdev.weatherforecast.features.city.data.db.CityDao
import com.wakeupdev.weatherforecast.features.city.data.db.CityEntity
import com.wakeupdev.weatherforecast.features.weather.data.db.WeatherDao
import com.wakeupdev.weatherforecast.features.weather.data.db.WeatherEntity

@Database(entities = [CityEntity::class, WeatherEntity::class], version = 1, exportSchema = false)
abstract class LocalDatabase : RoomDatabase() {

    abstract fun cityDao(): CityDao
    abstract fun weatherDao(): WeatherDao
}

