package com.wakeupdev.weatherforecast.data.api

class GeocodeRes : ArrayList<GeocodeResItem>()

data class GeocodeResItem(
    val country: String?,
    val lat: Double,
    val lon: Double,
    val name: String?,
    val state: String?
)

fun GeocodeResItem.toCityData(): City {
    return City(
        id = 0,
        name = name ?: "",
        state = state ?: "",
        country = country ?: "",
        latitude = lat,
        longitude = lon
    )
}