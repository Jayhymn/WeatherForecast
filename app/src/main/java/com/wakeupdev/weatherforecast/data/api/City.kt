package com.wakeupdev.weatherforecast.data.api

import android.os.Parcelable
import com.wakeupdev.weatherforecast.data.db.entities.CityEntity
import kotlinx.parcelize.Parcelize

@Parcelize
data class City(
    val id: Long,
    val name: String,
    val state: String = "Unknown",
    val country: String,
    val latitude: Double,
    val longitude: Double
) : Parcelable

fun City.toCityEntity(): CityEntity {
    return CityEntity(
        id = id,
        name = name,
        state = state,
        country = country,
        latitude = latitude,
        longitude = longitude
    )
}