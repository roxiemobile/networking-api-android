package com.roxiemobile.networkingapi.network.rest.config

import com.roxiemobile.networkingapi.network.ssl.TlsConfig
import okhttp3.CertificatePinner
import okhttp3.Interceptor
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

interface HttpClientConfig: Cloneable {

// MARK: - Properties

    /**
     * TODO
     */
    fun connectionTimeout(): Long

    /**
     * TODO
     */
    fun readTimeout(): Long

    /**
     * TODO
     */
    fun tlsConfig(): TlsConfig?

    /**
     * TODO
     */
    fun interceptors(): List<Interceptor>?

    /**
     * TODO
     */
    fun networkInterceptors(): List<Interceptor>?

    /**
     * TODO
     */
    fun certificatePinner(): CertificatePinner?

    /**
     * TODO
     */
    fun hostnameVerifier(): HostnameVerifier?

    /**
     * TODO
     */
    fun sslSocketFactory(): SSLSocketFactory?

    /**
     * TODO
     */
    fun trustManager(): X509TrustManager?

// MARK: - Methods

    /**
     * TODO
     */
    public override fun clone(): HttpClientConfig
}
