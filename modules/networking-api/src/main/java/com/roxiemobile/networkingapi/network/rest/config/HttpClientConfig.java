package com.roxiemobile.networkingapi.network.rest.config;

import java.util.List;

import okhttp3.Interceptor;

public interface HttpClientConfig
{
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
    List<Interceptor> interceptors();

    /**
     * TODO
     */
    List<Interceptor> networkInterceptors();
}
