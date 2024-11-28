package com.wakeupdev.weatherforecast.data

data class City(
    val name: String,
    val state: String  = "Unknown",
    val country: String,
    val latitude: Double,
    val longitude: Double
)