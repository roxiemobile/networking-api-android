package com.roxiemobile.networkingapi.network.rest.interceptor;

import com.annimon.stream.Stream;
import com.roxiemobile.networkingapi.network.http.HttpHeaders;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.Version;

/**
 * This interceptor adds a custom User-Agent.
 */
public final class UserAgentRequestInterceptor implements Interceptor {

// MARK: - Methods

    @Override
    public @NotNull Response intercept(@NotNull Chain chain) throws IOException {
        Request request = chain.request();

        Request newRequest = request.newBuilder()
                .header(HttpHeaders.USER_AGENT, createUserAgent(request.headers()))
                .build();

        return chain.proceed(newRequest);
    }

// MARK: - Private Methods

    private @NotNull String createUserAgent(@NotNull Headers headers) {

        String userAgentText = Stream.of(headers.values(HttpHeaders.USER_AGENT))
                .filterNot(s -> s.contains(OKHTTP_VERSION))
                .map(s -> s + " " + OKHTTP_VERSION)
                .single();

        return (userAgentText == null) ? OKHTTP_VERSION : userAgentText;
    }

// MARK: - Constants

    public static final @NotNull String OKHTTP_VERSION = Version.userAgent();
}
