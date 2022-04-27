package com.roxiemobile.networkingapi.network.rest.config;

import com.roxiemobile.androidcommons.logging.Logger;
import com.roxiemobile.androidcommons.logging.Logger.LogLevel;
import com.roxiemobile.networkingapi.network.NetworkConfig;
import com.roxiemobile.networkingapi.network.rest.interceptor.Interceptors;
import com.roxiemobile.networkingapi.network.rest.interceptor.UserAgentRequestInterceptor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.CertificatePinner;
import okhttp3.Interceptor;

public class DefaultHttpClientConfig implements HttpClientConfig {

// MARK: - Methods

    @Override
    public long connectionTimeout() {
        return NetworkConfig.Timeout.CONNECTION;
    }

    @Override
    public long readTimeout() {
        return NetworkConfig.Timeout.READ;
    }

    @Override
    public @NotNull List<Interceptor> interceptors() {
        return INTERCEPTORS;
    }

    @Override
    public @NotNull List<Interceptor> networkInterceptors() {
        return NETWORK_INTERCEPTORS;
    }

    @Override
    public @Nullable CertificatePinner certificatePinner() {
        return null;
    }

    @Nullable
    public @Override HostnameVerifier hostnameVerifier() {
        return null;
    }

    @Override
    public @Nullable SSLSocketFactory sslSocketFactory() {
        return null;
    }

    @Override
    public @Nullable X509TrustManager trustManager() {
        return null;
    }

// MARK: - Private Methods

    private static List<Interceptor> newNetworkInterceptors() {
        return Collections.unmodifiableList(new ArrayList<Interceptor>() {{

            // Interceptor which adds an OkHttp3 library's version to an User-Agent's header.
            add(new UserAgentRequestInterceptor());

            // Interceptor which logs request and response information.
            if (Logger.isLoggable(LogLevel.Debug)) {
                add(Interceptors.newHttpLoggingInterceptor());
            }
        }});
    }

// MARK: - Constants

    private static final List<Interceptor> INTERCEPTORS =
            Collections.emptyList();

    private static final List<Interceptor> NETWORK_INTERCEPTORS =
            newNetworkInterceptors();
}
