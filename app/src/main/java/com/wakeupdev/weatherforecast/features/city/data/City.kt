package com.wakeupdev.weatherforecast.features.city.data

data class City(
    val name: String,
    val state: String,
    val country: String,
    val latitude: Double,
    val longitude: Double
) {
}