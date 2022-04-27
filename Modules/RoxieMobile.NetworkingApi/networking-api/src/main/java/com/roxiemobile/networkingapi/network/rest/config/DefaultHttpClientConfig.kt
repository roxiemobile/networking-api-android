package com.roxiemobile.networkingapi.network.rest.config

import com.roxiemobile.androidcommons.logging.Logger
import com.roxiemobile.androidcommons.logging.Logger.LogLevel
import com.roxiemobile.networkingapi.network.NetworkConfig.Timeout
import com.roxiemobile.networkingapi.network.rest.interceptor.Interceptors
import com.roxiemobile.networkingapi.network.rest.interceptor.UserAgentRequestInterceptor
import com.roxiemobile.networkingapi.network.ssl.TlsConfig
import okhttp3.CertificatePinner
import okhttp3.Interceptor
import java.util.Collections
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

class DefaultHttpClientConfig: HttpClientConfig {

// MARK: - Methods

    override fun connectionTimeout(): Long {
        return Timeout.CONNECTION
    }

    override fun readTimeout(): Long {
        return Timeout.READ
    }

    override fun tlsConfig(): TlsConfig? {
        return null
    }

    override fun interceptors(): List<Interceptor>? {
        return null
    }

    override fun networkInterceptors(): List<Interceptor>? {
        return NETWORK_INTERCEPTORS
    }

    override fun certificatePinner(): CertificatePinner? {
        return null
    }

    override fun hostnameVerifier(): HostnameVerifier? {
        return null
    }

    override fun sslSocketFactory(): SSLSocketFactory? {
        return null
    }

    override fun trustManager(): X509TrustManager? {
        return null
    }

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
