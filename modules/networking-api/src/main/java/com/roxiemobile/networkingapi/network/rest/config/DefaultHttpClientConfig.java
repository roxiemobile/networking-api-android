package com.roxiemobile.networkingapi.network.rest.config;

import com.roxiemobile.androidcommons.logging.Logger;
import com.roxiemobile.androidcommons.logging.Logger.LogLevel;
import com.roxiemobile.networkingapi.network.NetworkConfig;
import com.roxiemobile.networkingapi.network.rest.interceptor.Interceptors;
import com.roxiemobile.networkingapi.network.rest.interceptor.UserAgentRequestInterceptor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Interceptor;

public class DefaultHttpClientConfig implements HttpClientConfig
{
// MARK: - Methods

    @Override
    public int connectTimeout() {
        return NetworkConfig.Timeout.CONNECTION;
    }

    @Override
    public int readTimeout() {
        return NetworkConfig.Timeout.READ;
    }

    @Override
    public List<Interceptor> interceptors() {
        return INTERCEPTORS;
    }

    @Override
    public List<Interceptor> networkInterceptors() {
        return NETWORK_INTERCEPTORS;
    }

// MARK: - Private Methods

    private static List<Interceptor> newNetworkInterceptors()
    {
        return Collections.unmodifiableList(new ArrayList<Interceptor>()
        {{
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
