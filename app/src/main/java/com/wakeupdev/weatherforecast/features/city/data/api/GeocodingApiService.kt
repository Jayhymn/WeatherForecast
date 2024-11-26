package com.wakeupdev.weatherforecast.features.city.data.api

import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingApiService {

    @GET("geo/1.0/direct")
    suspend fun getGeocodes(
        @Query("q") cityName: String,
        @Query("limit") limit: Int = 4,
    ): GeocodeRes
}