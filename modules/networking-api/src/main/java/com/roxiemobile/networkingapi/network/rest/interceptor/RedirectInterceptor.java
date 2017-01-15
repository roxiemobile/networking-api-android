package com.roxiemobile.networkingapi.network.rest.interceptor;

import android.support.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public interface RedirectInterceptor extends Interceptor
{
// MARK: - Methods

    /**
     * TODO
     */
    Response intercept(Chain chain) throws IOException;

    /**
     * TODO
     */
    Response onRedirect(@NonNull Response response) throws IOException;
}
