package com.wakeupdev.weatherforecast.data.api

import com.wakeupdev.weatherforecast.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response


class ApiKeyInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalUrl = originalRequest.url()

        // Add API key to the query parameters
        val newUrl = originalUrl.newBuilder()
            .addQueryParameter("appid", BuildConfig.API_KEY) // Add API key
            .build()

        // Build the new request with the updated URL
        val newRequest = originalRequest.newBuilder()
            .url(newUrl)
            .build()

        return chain.proceed(newRequest)
    }
}

