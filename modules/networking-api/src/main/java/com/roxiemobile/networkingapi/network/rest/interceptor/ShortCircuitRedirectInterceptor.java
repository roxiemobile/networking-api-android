package com.roxiemobile.networkingapi.network.rest.interceptor;

import android.support.annotation.NonNull;

import com.roxiemobile.networkingapi.network.rest.RestApiClient.HttpResponseException;

import java.io.IOException;

import okhttp3.Response;

public class ShortCircuitRedirectInterceptor extends AbstractRedirectInterceptor
{
// MARK: - Methods

    /**
     * TODO
     */
    public Response onRedirect(@NonNull Response response) throws IOException {
        // Throw an exception on redirects
        throw new HttpResponseException(decompressResponse(response));
    }
}
