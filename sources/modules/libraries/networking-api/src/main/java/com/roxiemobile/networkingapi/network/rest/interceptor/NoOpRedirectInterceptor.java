package com.roxiemobile.networkingapi.network.rest.interceptor;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Response;

public final class NoOpRedirectInterceptor extends AbstractRedirectInterceptor {

// MARK: - Methods

    /**
     * TODO
     */
    public @NotNull Response onRedirect(@NotNull Response response) throws IOException {
        return response;
    }
}
