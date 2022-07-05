package com.roxiemobile.networkingapi.network.rest.configuration

import com.roxiemobile.networkingapi.network.ssl.TlsConfig
import okhttp3.Interceptor

interface HttpClientConfig: Cloneable {

// MARK: - Properties

    /**
     * TODO
     */
    val requestTimeoutConfig: RequestTimeoutConfig?

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
