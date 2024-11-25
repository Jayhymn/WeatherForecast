package com.wakeupdev.weatherforecast.data.db.city

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cities")
data class CityEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,  // Auto-generated primary key
    @ColumnInfo(name = "name") val name: String,        // Name of the city
    @ColumnInfo(name = "latitude") val latitude: Double, // Latitude of the city
    @ColumnInfo(name = "longitude") val longitude: Double // Longitude of the city
)
