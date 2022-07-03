package com.roxiemobile.networkingapi.network.rest.interceptor;

import com.roxiemobile.androidcommons.diagnostics.Guard;
import com.roxiemobile.androidcommons.util.StringUtils;
import com.roxiemobile.networkingapi.network.http.HttpHeaders;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.RealResponseBody;
import okio.GzipSource;
import okio.Okio;

public abstract class AbstractRedirectInterceptor implements Interceptor {

// MARK: - Methods

    @Override
    public @NotNull Response intercept(@NotNull Chain chain) throws IOException {
        Request request = chain.request();

        // Execute HTTP request
        Response response = chain.proceed(request);
        if (response.isRedirect()) {

            // Handle HTTP redirects
            response = onRedirect(response);
        }

        // Done
        return response;
    }

    /**
     * TODO
     */
    public abstract @NotNull Response onRedirect(@NotNull Response response) throws IOException;

    /**
     * Returns a new response that does gzip decompression on {@code response}.
     */
    protected @NotNull Response decompressResponse(@NotNull Response response) throws IOException {
        Guard.notNull(response, "response is null");

        ResponseBody responseBody = response.body();
        if (responseBody != null && "gzip".equalsIgnoreCase(response.header(HttpHeaders.CONTENT_ENCODING))) {

            // Uncompress a response body
            GzipSource gzipSource = new GzipSource(responseBody.source());
            Headers strippedHeaders = response.headers().newBuilder()
                    .removeAll(HttpHeaders.CONTENT_ENCODING)
                    .removeAll(HttpHeaders.CONTENT_LENGTH)
                    .build();

            // Create new HTTP response
            ResponseBody newResponseBody = new RealResponseBody(
                    strippedHeaders.get(HttpHeaders.CONTENT_TYPE),
                    stringToLong(strippedHeaders.get(HttpHeaders.CONTENT_LENGTH)),
                    Okio.buffer(gzipSource));

            response = response.newBuilder()
                    .headers(strippedHeaders)
                    .body(ResponseBody.create(newResponseBody.contentType(), newResponseBody.bytes()))
                    .build();
        }

        // Done
        return response;
    }

// MARK: - Private Methods

    private static long stringToLong(@Nullable String str) {
        long value = -1L;

        if (StringUtils.isNotBlank(str)) {
            try {
                value = Long.parseLong(str);
            }
            catch (NumberFormatException ex) {
                // Do nothing
            }
        }
        return value;
    }
}
