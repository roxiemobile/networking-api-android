package com.roxiemobile.networkingapi.network.rest.config

import com.roxiemobile.networkingapi.network.ssl.TlsConfig
import okhttp3.Interceptor

interface HttpClientConfig: Cloneable {

// MARK: - Properties

    /**
     * TODO
     */
    val connectionTimeout: Long

    /**
     * TODO
     */
    val readTimeout: Long

    /**
     * TODO
     */
    val tlsConfig: TlsConfig?

    /**
     * TODO
     */
    val interceptors: List<Interceptor>?

    /**
     * TODO
     */
    val networkInterceptors: List<Interceptor>?

// MARK: - Methods

    /**
     * TODO
     */
    public override fun clone(): HttpClientConfig
}
