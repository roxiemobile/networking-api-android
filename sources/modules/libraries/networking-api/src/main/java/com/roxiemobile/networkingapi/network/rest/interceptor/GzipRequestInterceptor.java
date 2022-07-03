package com.roxiemobile.networkingapi.network.rest.interceptor;

import com.roxiemobile.networkingapi.network.http.ContentCodingType;
import com.roxiemobile.networkingapi.network.http.HttpHeaders;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSink;
import okio.GzipSink;
import okio.Okio;

/**
 * This interceptor compresses the HTTP request body.
 */
public final class GzipRequestInterceptor implements Interceptor {

// MARK: - Methods

    @Override
    public @NotNull Response intercept(@NotNull Chain chain) throws IOException {
        Request request = chain.request();

        if (request.body() == null || request.header(HttpHeaders.CONTENT_ENCODING) != null) {
            return chain.proceed(request);
        }

        Request newRequest = request.newBuilder()
                .header(HttpHeaders.CONTENT_ENCODING, ContentCodingType.GZIP_VALUE)
                .method(request.method(), requestBodyWithContentLength(gzip(request.body())))
                .build();

        return chain.proceed(newRequest);
    }

// MARK: - Private Methods

    private @NotNull RequestBody gzip(final @Nullable RequestBody requestBody) {
        return new RequestBody() {

            @Override
            public @Nullable MediaType contentType() {
                return requestBody.contentType();
            }

            @Override
            public long contentLength() {
                // NOTE: We don't know the compressed length in advance!
                return -1;
            }

            @Override
            public void writeTo(@NotNull BufferedSink sink) throws IOException {
                BufferedSink gzipSink = Okio.buffer(new GzipSink(sink));
                requestBody.writeTo(gzipSink);
                gzipSink.close();
            }
        };
    }

    // Add GZip Request Compression
    // @link https://github.com/square/okhttp/issues/350#issuecomment-123105641

    private RequestBody requestBodyWithContentLength(final @NotNull RequestBody requestBody) throws IOException {

        final @NotNull Buffer buffer = new Buffer();
        requestBody.writeTo(buffer);

        return new RequestBody() {

            @Override
            public @Nullable MediaType contentType() {
                return requestBody.contentType();
            }

            @Override
            public long contentLength() {
                return buffer.size();
            }

            @Override
            public void writeTo(@NotNull BufferedSink sink) throws IOException {
                sink.write(buffer.snapshot());
            }
        };
    }
}
