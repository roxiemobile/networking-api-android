package com.roxiemobile.networkingapi.network.rest.interceptor;

import com.roxiemobile.networkingapi.network.rest.RestApiClient.HttpResponseException;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Response;

public class ShortCircuitRedirectInterceptor extends AbstractRedirectInterceptor {

// MARK: - Methods

    /**
     * TODO
     */
    public @NotNull Response onRedirect(@NotNull Response response) throws IOException {
        // Throw an exception on redirects
        throw new HttpResponseException(decompressResponse(response));
    }
}
