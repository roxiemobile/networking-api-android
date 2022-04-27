package com.roxiemobile.networkingapi.network.rest.interceptor

import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level

object Interceptors {

// MARK: - Methods

    @JvmOverloads
    fun createHttpLoggingInterceptor(level: Level = Level.BODY): Interceptor {

        return HttpLoggingInterceptor().apply {
            // Log request and response lines and their respective headers and bodies (if present)
            this.level = level
        }
    }
}
