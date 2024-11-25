package com.wakeupdev.weatherforecast.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.wakeupdev.weatherforecast.data.db.city.CityDao
import com.wakeupdev.weatherforecast.data.db.city.CityEntity
import com.wakeupdev.weatherforecast.data.db.weather.WeatherDao
import com.wakeupdev.weatherforecast.data.db.weather.WeatherEntity

@Database(entities = [CityEntity::class, WeatherEntity::class], version = 1, exportSchema = false)
abstract class WeatherDb : RoomDatabase() {

    abstract fun cityDao(): CityDao
    abstract fun weatherDao(): WeatherDao

    companion object {
//        @Volatile
//        private var INSTANCE: WeatherDatabase? = null
//
//        fun getDatabase(context: Context): WeatherDatabase {
//            return INSTANCE ?: synchronized(this) {
//                val instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    WeatherDatabase::class.java,
//                    "weather_database"
//                ).build()
//                INSTANCE = instance
//                instance
//            }
//        }
    }
}

