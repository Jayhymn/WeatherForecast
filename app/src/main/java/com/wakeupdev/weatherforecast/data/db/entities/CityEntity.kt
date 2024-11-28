package com.wakeupdev.weatherforecast.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.wakeupdev.weatherforecast.data.api.City

@Entity(tableName = "cities", indices = [Index(value = ["name"], unique = true)])
data class CityEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,  // Auto-generated primary key
    @ColumnInfo(name = "name") val name: String,        // Name of the city
    @ColumnInfo(name = "state") val state: String,        // Name of the state
    @ColumnInfo(name = "country") val country: String,        // Name of the country
    @ColumnInfo(name = "latitude") val latitude: Double, // Latitude of the city
    @ColumnInfo(name = "longitude") val longitude: Double // Longitude of the city
)

fun CityEntity.toCityData(): City {
    return City(
        name = "$name,$state, $country",
        state = state,
        country = country,
        latitude = latitude,
        longitude = longitude
    )
}
