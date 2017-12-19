package com.roxiemobile.networkingapi.network.rest.interceptor;

import android.support.annotation.NonNull;

import java.io.IOException;

import okhttp3.Response;

public class NoopRedirectInterceptor extends AbstractRedirectInterceptor
{
// MARK: - Methods

    /**
     * TODO
     */
    public Response onRedirect(@NonNull Response response) throws IOException {
        return response;
    }
}
