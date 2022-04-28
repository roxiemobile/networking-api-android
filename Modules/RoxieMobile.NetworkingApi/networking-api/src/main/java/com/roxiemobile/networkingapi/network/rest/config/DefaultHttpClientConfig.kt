package com.roxiemobile.networkingapi.network.rest.config

import com.roxiemobile.androidcommons.logging.Logger
import com.roxiemobile.androidcommons.logging.Logger.LogLevel
import com.roxiemobile.networkingapi.network.NetworkConfig.Timeout
import com.roxiemobile.networkingapi.network.rest.interceptor.Interceptors
import com.roxiemobile.networkingapi.network.rest.interceptor.UserAgentRequestInterceptor
import com.roxiemobile.networkingapi.network.ssl.TlsConfig
import okhttp3.Interceptor
import java.util.Collections

class DefaultHttpClientConfig: HttpClientConfig {

// MARK: - Methods

    override val connectionTimeout: Long = Timeout.CONNECTION

    override val readTimeout: Long = Timeout.READ

    override val tlsConfig: TlsConfig? = null

    override val interceptors: List<Interceptor>? = null

    override val networkInterceptors: List<Interceptor>? = NETWORK_INTERCEPTORS

// MARK: - Methods

    override fun clone(): HttpClientConfig {
        return DefaultHttpClientConfig()
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
    }
}
