package com.roxiemobile.networkingapi.network.rest.configuration

import com.roxiemobile.androidcommons.logging.Logger
import com.roxiemobile.androidcommons.logging.Logger.LogLevel
import com.roxiemobile.networkingapi.network.rest.interceptor.Interceptors
import com.roxiemobile.networkingapi.network.rest.interceptor.UserAgentRequestInterceptor
import com.roxiemobile.networkingapi.network.ssl.TlsConfig
import okhttp3.Interceptor
import java.util.Collections

data class DefaultHttpClientConfig(
    override val requestTimeoutConfig: RequestTimeoutConfig? = DefaultRequestTimeoutConfig.SHARED,
    override val tlsConfig: TlsConfig? = null,
    override val interceptors: List<Interceptor>? = null,
    override val networkInterceptors: List<Interceptor>? = NETWORK_INTERCEPTORS,
): HttpClientConfig {

// MARK: - Methods

    override fun clone(): HttpClientConfig {
        return copy()
    }

// MARK: - Companion

    companion object {

        private val NETWORK_INTERCEPTORS = Collections.unmodifiableList(
            mutableListOf<Interceptor>().apply {

                // Interceptor which adds an OkHttp3 library's version to an User-Agent's header
                add(UserAgentRequestInterceptor())

                // Interceptor which logs request and response information
                if (Logger.isLoggable(LogLevel.Debug)) {
                    add(Interceptors.createHttpLoggingInterceptor())
                }
            }
        )

        @JvmField
        val SHARED = DefaultHttpClientConfig()
    }
}
