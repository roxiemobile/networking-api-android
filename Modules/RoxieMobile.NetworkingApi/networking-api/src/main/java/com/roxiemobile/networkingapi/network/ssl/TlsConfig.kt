package com.roxiemobile.networkingapi.network.ssl

import okhttp3.CertificatePinner
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

interface TlsConfig: Cloneable {

// MARK: - Properties

    /**
     * TODO
     */
    val certificatePinner: CertificatePinner?

    /**
     * TODO
     */
    val hostnameVerifier: HostnameVerifier?

    /**
     * TODO
     */
    val sslSocketFactory: SSLSocketFactory?

    /**
     * TODO
     */
    val trustManager: X509TrustManager?

// MARK: - Methods

    /**
     * TODO
     */
    public override fun clone(): TlsConfig
}
