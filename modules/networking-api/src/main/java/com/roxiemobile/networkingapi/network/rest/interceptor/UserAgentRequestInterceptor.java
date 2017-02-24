package com.roxiemobile.networkingapi.network.rest.interceptor;

import com.annimon.stream.Stream;
import com.roxiemobile.networkingapi.network.http.HttpHeaders;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.Version;

/**
 * This interceptor adds a custom User-Agent.
 */
public class UserAgentRequestInterceptor implements Interceptor
{
// MARK: - Methods

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        Request updatedRequest = originalRequest.newBuilder()
                .header(HttpHeaders.USER_AGENT, newUserAgent(originalRequest.headers()))
                .build();

        return chain.proceed(updatedRequest);
    }

// MARK: - Private Methods

    private String newUserAgent(Headers headers)
    {
        String value = Stream.of(headers.values(HttpHeaders.USER_AGENT))
                             .filterNot(s -> s.contains(OKHTTP_VERSION))
                             .map(s -> s + " " + OKHTTP_VERSION)
                             .single();

        return (value == null) ? OKHTTP_VERSION : value;
    }

// MARK: - Constants

    public static final String OKHTTP_VERSION = Version.userAgent();
}
