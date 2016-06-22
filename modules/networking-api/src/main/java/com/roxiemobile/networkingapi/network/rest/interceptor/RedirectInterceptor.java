package com.roxiemobile.networkingapi.network.rest.interceptor;

import android.support.annotation.NonNull;

import com.roxiemobile.networkingapi.network.http.HttpHeaders;
import com.roxiemobile.networkingapi.network.rest.RestApiClient.HttpResponseException;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.RealResponseBody;
import okio.GzipSource;
import okio.Okio;

import static com.roxiemobile.androidcommons.util.AssertUtils.assertNotNull;

public class RedirectInterceptor implements Interceptor
{
// MARK: - Methods

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        // Execute a HTTP request
        Response response = chain.proceed(request);
        if (response.isRedirect()) {

            // Throw an exception on redirects
            throw new HttpResponseException(unzip(response));
        }

        // Done
        return response;
    }

// MARK: - Private Methods

    /**
     * Returns a new response that does gzip decompression on {@code response}.
     */
    private Response unzip(@NonNull Response response) throws IOException {
        assertNotNull(response, "response == null");

        ResponseBody body = response.body();
        if (body != null && "gzip".equalsIgnoreCase(response.header(HttpHeaders.CONTENT_ENCODING))) {

            // Uncompress a response body
            GzipSource source = new GzipSource(body.source());
            Headers strippedHeaders = response.headers().newBuilder()
                    .removeAll(HttpHeaders.CONTENT_ENCODING)
                    .removeAll(HttpHeaders.CONTENT_LENGTH)
                    .build();

            // Create new HTTP response
            ResponseBody responseBody = new RealResponseBody(strippedHeaders, Okio.buffer(source));
            response = response.newBuilder()
                    .headers(strippedHeaders)
                    .body(ResponseBody.create(responseBody.contentType(), responseBody.bytes()))
                    .build();
        }

        // Done
        return response;
    }

}
