package com.roxiemobile.networkingapi.network.rest;

import com.annimon.stream.Objects;
import com.annimon.stream.Stream;
import com.roxiemobile.androidcommons.diagnostics.Guard;
import com.roxiemobile.androidcommons.logging.Logger;
import com.roxiemobile.androidcommons.util.ArrayUtils;
import com.roxiemobile.networkingapi.network.http.CompatJavaNetCookieJar;
import com.roxiemobile.networkingapi.network.http.CookieManager;
import com.roxiemobile.networkingapi.network.http.CookiePolicy;
import com.roxiemobile.networkingapi.network.http.CookieStore;
import com.roxiemobile.networkingapi.network.http.HttpHeaders;
import com.roxiemobile.networkingapi.network.http.HttpMethod;
import com.roxiemobile.networkingapi.network.http.HttpStatus;
import com.roxiemobile.networkingapi.network.http.InMemoryCookieStore;
import com.roxiemobile.networkingapi.network.http.MediaType;
import com.roxiemobile.networkingapi.network.rest.configuration.DefaultHttpClientConfig;
import com.roxiemobile.networkingapi.network.rest.configuration.DefaultRequestTimeoutConfig;
import com.roxiemobile.networkingapi.network.rest.configuration.HttpClientConfig;
import com.roxiemobile.networkingapi.network.rest.configuration.RequestTimeoutConfig;
import com.roxiemobile.networkingapi.network.rest.request.ByteArrayBody;
import com.roxiemobile.networkingapi.network.rest.request.RequestEntity;
import com.roxiemobile.networkingapi.network.rest.response.BasicResponseEntity;
import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity;
import com.roxiemobile.networkingapi.network.ssl.TlsConfig;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.CertificatePinner;
import okhttp3.CookieJar;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public final class RestApiClient {

// MARK: - Construction

    private RestApiClient(@NotNull Builder builder) {
        mHttpClientConfig = builder.mHttpClientConfig.clone();
    }

// MARK: - Methods

    public @NotNull HttpResult get(@NotNull RequestEntity<HttpBody> requestEntity) {
        return execute(HttpMethod.GET, requestEntity);
    }

    public @NotNull HttpResult post(@NotNull RequestEntity<HttpBody> requestEntity) {
        return execute(HttpMethod.POST, requestEntity);
    }

    public @NotNull HttpResult put(@NotNull RequestEntity<HttpBody> requestEntity) {
        return execute(HttpMethod.PUT, requestEntity);
    }

    public @NotNull HttpResult patch(@NotNull RequestEntity<HttpBody> requestEntity) {
        return execute(HttpMethod.PATCH, requestEntity);
    }

    public @NotNull HttpResult delete(@NotNull RequestEntity<HttpBody> requestEntity) {
        return execute(HttpMethod.DELETE, requestEntity);
    }

    public @NotNull HttpResult head(@NotNull RequestEntity<HttpBody> requestEntity) {
        return execute(HttpMethod.HEAD, requestEntity);
    }

    public @NotNull HttpResult options(@NotNull RequestEntity<HttpBody> requestEntity) {
        return execute(HttpMethod.OPTIONS, requestEntity);
    }

// MARK: - Private Methods

    private @NotNull HttpResult execute(@NotNull HttpMethod httpMethod, @NotNull RequestEntity<HttpBody> requestEntity) {
        Guard.notNull(httpMethod, "httpMethod is null");
        Guard.notNull(requestEntity, "requestEntity is null");

        // Execute HTTP request
        return execute(createRequest(httpMethod, requestEntity), requestEntity.getCookieStore());
    }

    private @NotNull HttpResult execute(@NotNull Request request, @Nullable CookieStore cookieStore) {
        cookieStore = (cookieStore != null) ? cookieStore : new InMemoryCookieStore();
        @NotNull HttpResult httpResult;

        try {
            try {
                // Create and execute HTTP request
                @NotNull Response response = createClient(cookieStore).newCall(request).execute();
                httpResult = HttpResult.success(createResponseEntity(response, cookieStore));
            }
            catch (HttpResponseException ex) {
                Logger.e(TAG, ex);

                // Handle interrupted HTTP requests
                httpResult = HttpResult.success(createResponseEntity(ex.getResponse(), cookieStore));
            }
        }
        catch (Exception ex) {
            Logger.e(TAG, ex);

            // Handle any other errors
            httpResult = HttpResult.failure(ex);
        }

        // Done
        return httpResult;
    }

    private @NotNull Request createRequest(@NotNull HttpMethod httpMethod, @NotNull RequestEntity<HttpBody> requestEntity) {
        @NotNull Request.Builder instance = new Request.Builder();

        // Create request body
        @Nullable HttpBody httpBody = requestEntity.getBody();
        @Nullable RequestBody requestBody = null;

        // NOTE: Workaround for OkHttp crash on POST with empty request body
        if (httpBody == null && httpMethod == HttpMethod.POST) {
            httpBody = EMPTY_HTTP_BODY;
        }

        if (httpBody != null) {
            @Nullable okhttp3.MediaType mediaType = okhttp3.MediaType.parse(httpBody.getMediaType().toString());
            requestBody = RequestBody.create(mediaType, httpBody.getBody());
        }

        // Build HTTP request
        @Nullable HttpHeaders httpHeaders = requestEntity.getHttpHeaders();

        instance.method(httpMethod.getValue(), requestBody)
                .url(requestEntity.getLink().toString())
                .headers(mapping(httpHeaders));

        if (requestBody != null) {
            @Nullable okhttp3.MediaType contentType = requestBody.contentType();

            if (contentType != null) {
                instance.header(HttpHeaders.CONTENT_TYPE, contentType.toString());
            }
        }

        return instance.build();
    }

    private @NotNull OkHttpClient createClient(@NotNull CookieStore cookieStore) {
        Guard.notNull(cookieStore, "cookieStore is null");

        @NotNull CookieManager cookieManager = new CookieManager(cookieStore, CookiePolicy.ACCEPT_ALL);
        @NotNull CookieJar cookieJar = new CompatJavaNetCookieJar(cookieManager);

        // Retrofit 2.0: The biggest update yet on the best HTTP Client Library for Android
        // @link http://inthecheesefactory.com/blog/retrofit-2.0/en
        // +> Certificate Pinning
        // +> RxJava Integration with CallAdapter

        @NotNull RequestTimeoutConfig requestTimeoutConfig = Optional
                .ofNullable(mHttpClientConfig.getRequestTimeoutConfig())
                .orElse(DefaultRequestTimeoutConfig.SHARED);

        @NotNull OkHttpClient.Builder builder = SHARED_HTTP_CLIENT.newBuilder()
                // Set the timeout until a connection is established
                .connectTimeout(requestTimeoutConfig.getConnectionTimeout(), TimeUnit.MILLISECONDS)
                // Set the default socket timeout which is the timeout for waiting for data
                .readTimeout(requestTimeoutConfig.getReadTimeout(), TimeUnit.MILLISECONDS)
                // Set the handler that can accept cookies from incoming HTTP responses and provides cookies to outgoing HTTP requests
                .cookieJar(cookieJar);

        // Set a application interceptors
        Stream.of(nullToEmpty(mHttpClientConfig.getInterceptors()))
                .filter(Objects::nonNull).forEach(builder::addInterceptor);

        // Set a network interceptors
        Stream.of(nullToEmpty(mHttpClientConfig.getNetworkInterceptors()))
                .filter(Objects::nonNull).forEach(builder::addNetworkInterceptor);

        // Configure secure HTTPS connections
        @Nullable TlsConfig tlsConfig = mHttpClientConfig.getTlsConfig();
        if (tlsConfig != null) {

            @Nullable CertificatePinner certificatePinner = tlsConfig.getCertificatePinner();
            if (certificatePinner != null) {
                builder.certificatePinner(certificatePinner);
            }

            @Nullable HostnameVerifier hostnameVerifier = tlsConfig.getHostnameVerifier();
            if (hostnameVerifier != null) {
                builder.hostnameVerifier(hostnameVerifier);
            }

            @Nullable SSLSocketFactory sslSocketFactory = tlsConfig.getSslSocketFactory();
            if (sslSocketFactory != null) {

                @Nullable X509TrustManager trustManager = tlsConfig.getTrustManager();
                if (trustManager != null) {
                    builder.sslSocketFactory(sslSocketFactory, trustManager);
                }
                else {
                    //noinspection deprecation
                    builder.sslSocketFactory(sslSocketFactory);
                }
            }
        }

        return builder.build();
    }

    private @NotNull ResponseEntity<byte[]> createResponseEntity(
            @NotNull Response response,
            @NotNull CookieStore cookieStore
    ) throws IOException {

        Guard.notNull(response, "response is null");
        Guard.notNull(cookieStore, "cookieStore is null");

        // Handle HTTP response
        @NotNull HttpStatus httpStatus = HttpStatus.valueOf(response.code());
        @NotNull BasicResponseEntity.Builder<byte[]> builder = new BasicResponseEntity.Builder<byte[]>()
                .link(response.request().url().uri())
                .httpHeaders(mapping(response.headers()))
                .httpStatus(httpStatus)
                // @see https://tools.ietf.org/html/rfc7231#section-3.1.1.5
                .mediaType(MediaType.APPLICATION_OCTET_STREAM)
                .cookieStore(cookieStore);

        @Nullable ResponseBody responseBody = response.body();
        if (responseBody != null) {

            // Set MediaType
            @Nullable okhttp3.MediaType contentType = responseBody.contentType();
            if (contentType != null) {
                builder.mediaType(MediaType.valueOf(contentType.toString()));
            }

            // Set response body
            builder.body(ArrayUtils.emptyToNull(responseBody.bytes()));
        }

        // Done
        return builder.build();
    }

    @Deprecated
    public @NotNull HttpHeaders mapping(@Nullable Headers headers) {
        @NotNull HttpHeaders httpHeaders = new HttpHeaders();

        // Map okhttp3.Headers to HttpHeaders
        if (headers != null && headers.size() > 0) {
            for (String name : headers.names()) {
                httpHeaders.put(name, headers.values(name));
            }
        }

        // Done
        return httpHeaders;
    }

    @Deprecated
    public @NotNull Headers mapping(@Nullable HttpHeaders headers) {
        @NotNull Headers.Builder builder = new Headers.Builder();

        // Map HttpHeaders to okhttp3.Headers
        if (headers != null && headers.size() > 0) {
            for (Entry<String, List<String>> entry : headers.entrySet()) {
                String key = entry.getKey();
                for (String value : entry.getValue()) {
                    builder.add(key, value);
                }
            }
        }

        // Done
        return builder.build();
    }

// MARK: - Private Methods

    private @NotNull <T> List<T> nullToEmpty(@Nullable List<T> list) {
        return (list != null) ? list : Collections.emptyList();
    }

// MARK: - Inner Types

    public static final class Builder {

        public Builder() {
            mHttpClientConfig = DefaultHttpClientConfig.SHARED;
        }

        public @NotNull Builder httpClientConfig(@Nullable HttpClientConfig httpClientConfig) {
            mHttpClientConfig = (httpClientConfig != null) ? httpClientConfig.clone() : DefaultHttpClientConfig.SHARED;
            return this;
        }

        public @NotNull RestApiClient build() {
            return new RestApiClient(this);
        }

        private @NotNull HttpClientConfig mHttpClientConfig;
    }

    public static final class HttpResponseException extends IOException {

        public HttpResponseException(@NotNull Response response) {
            mResponse = response;
        }

        public @NotNull Response getResponse() {
            return mResponse;
        }

        private final @NotNull Response mResponse;
    }

// MARK: - Constants

    private static final @NotNull String TAG = RestApiClient.class.getSimpleName();

    private static final @NotNull OkHttpClient SHARED_HTTP_CLIENT = new OkHttpClient.Builder().build();
    private static final @NotNull HttpBody EMPTY_HTTP_BODY = new ByteArrayBody();

// MARK: - Variables

    private final @NotNull HttpClientConfig mHttpClientConfig;
}
