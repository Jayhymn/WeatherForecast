package com.wakeupdev.weatherforecast.features.weather.data.api

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("data/3.0/onecall")
    suspend fun getWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("cnt") numberOfDays: Int = 7,
    ): WeatherResponse

//    @GET("data/2.5/group")
//    suspend fun getWeatherForCities(
//        @Query("id") cityIds: String, // Comma-separated city IDs
//        @Query("units") units: String = "metric",
//    ): WeatherResponse
}
