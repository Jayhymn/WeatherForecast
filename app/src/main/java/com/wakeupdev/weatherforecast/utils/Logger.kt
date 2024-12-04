package com.wakeupdev.weatherforecast.utils
import android.util.Log
import javax.inject.Inject

interface Logger {
    fun e(tag: String, message: String, throwable: Throwable? = null)
    fun d(tag: String, message: String)
    fun i(tag: String, message: String)
    fun w(tag: String, message: String)
}