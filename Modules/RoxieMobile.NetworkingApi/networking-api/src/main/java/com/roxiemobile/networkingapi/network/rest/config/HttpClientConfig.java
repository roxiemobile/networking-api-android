package com.roxiemobile.networkingapi.network.rest.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.CertificatePinner;
import okhttp3.Interceptor;

public interface HttpClientConfig {

// MARK: - Methods

    /**
     * TODO
     */
    int connectTimeout();

    /**
     * TODO
     */
    int readTimeout();

    /**
     * TODO
     */
    @NotNull List<Interceptor> interceptors();

    /**
     * TODO
     */
    @NotNull List<Interceptor> networkInterceptors();

    /**
     * TODO
     */
    default @Nullable CertificatePinner certificatePinner() {
        return null;
    }

    /**
     * TODO
     */
    default @Nullable HostnameVerifier hostnameVerifier() {
        return null;
    }

    /**
     * TODO
     */
    default @Nullable SSLSocketFactory sslSocketFactory() {
        return null;
    }

    /**
     * TODO
     */
    default @Nullable X509TrustManager trustManager() {
        return null;
    }
}
