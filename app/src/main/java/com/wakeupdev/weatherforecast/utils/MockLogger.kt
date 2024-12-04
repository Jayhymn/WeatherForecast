package com.wakeupdev.weatherforecast.utils

class MockLogger : Logger {
    val logMessages = mutableListOf<String>()

    override fun e(tag: String, message: String, throwable: Throwable?) {
        logMessages.add("ERROR: $message")
    }

    override fun d(tag: String, message: String) {
        logMessages.add("DEBUG: $message")
    }

    override fun i(tag: String, message: String) {
        logMessages.add("INFO: $message")
    }

    override fun w(tag: String, message: String) {
        logMessages.add("WARN: $message")
    }
}