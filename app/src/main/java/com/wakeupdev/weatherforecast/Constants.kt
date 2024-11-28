package com.wakeupdev.weatherforecast

object Constants {
    const val ARG_WEATHER_DATA = "args_weather_data"
    const val SYNC_NOTIFICATION_CHANNEL_DESCRIPTION = "Refreshing weather forecast synchronization from remote api"
    const val SYNC_NOTIFICATION_CHANNEL_NAME = "WeatherForecast Synchronization"
    const val SYNC_NOTIFICATION_CHANNEL_ID = "100"
    const val BASE_URL = "https://api.openweathermap.org/"
    const val ICON_URL = "https://openweathermap.org/img/wn/"
    const val REQUEST_TIMEOUT = 30L
    const val LOCATION_PERMISSION_REQUEST_CODE = 105
}