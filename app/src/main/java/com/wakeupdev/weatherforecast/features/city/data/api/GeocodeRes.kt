package com.wakeupdev.weatherforecast.features.city.data.api

import com.wakeupdev.weatherforecast.features.city.data.City

class GeocodeRes : ArrayList<GeocodeResItem>()

data class GeocodeResItem(
    val country: String,
    val lat: Double,
    val lon: Double,
    val name: String,
    val state: String
)

fun GeocodeResItem.toCityData(): City {
    return City(
        name = name,
        state = state,
        country = country,
        latitude = lat,
        longitude = lon
    )
}