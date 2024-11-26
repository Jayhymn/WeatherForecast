package com.wakeupdev.weatherforecast.shared

object Constants {
    const val SYNC_NOTIFICATION_CHANNEL_DESCRIPTION = "Refreshing weather forecast synchronization from remote api"
    const val SYNC_NOTIFICATION_CHANNEL_NAME = "WeatherForecast Synchronization"
    const val SYNC_NOTIFICATION_CHANNEL_ID = "100"
    const val BASE_URL = "https://api.openweathermap.org/"
    const val REQUEST_TIMEOUT = 30L
}