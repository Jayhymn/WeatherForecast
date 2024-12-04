package com.wakeupdev.weatherforecast.utils

import android.util.Log
import javax.inject.Inject

open class LoggerImpl @Inject constructor() : Logger {
    override fun e(tag: String, message: String, throwable: Throwable?) {
        Log.e(tag, message, throwable)
    }

    override fun d(tag: String, message: String) {
        Log.d(tag, message)
    }

    override fun i(tag: String, message: String) {
        Log.i(tag, message)
    }

    override fun w(tag: String, message: String) {
        Log.w(tag, message)
    }
}