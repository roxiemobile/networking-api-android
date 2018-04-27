package com.roxiemobile.networkingapi.network.rest.interceptor;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Response;

public class NoopRedirectInterceptor extends AbstractRedirectInterceptor
{
// MARK: - Methods

    /**
     * TODO
     */
    public Response onRedirect(@NotNull Response response) throws IOException {
        return response;
    }
}
