package com.wakeupdev.weatherforecast.data.api

import com.wakeupdev.weatherforecast.data.api.models.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("data/2.5/forecast/daily")
    suspend fun getWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("cnt") numberOfDays: Int = 7,
    ): WeatherResponse
}
