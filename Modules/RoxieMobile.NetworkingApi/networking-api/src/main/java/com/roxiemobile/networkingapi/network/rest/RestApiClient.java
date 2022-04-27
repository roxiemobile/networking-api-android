package com.roxiemobile.networkingapi.network.rest;

import com.annimon.stream.Objects;
import com.annimon.stream.Stream;
import com.roxiemobile.androidcommons.diagnostics.Guard;
import com.roxiemobile.androidcommons.logging.Logger;
import com.roxiemobile.androidcommons.util.ArrayUtils;
import com.roxiemobile.networkingapi.network.HttpKeys.MethodName;
import com.roxiemobile.networkingapi.network.NetworkConfig;
import com.roxiemobile.networkingapi.network.http.CompatJavaNetCookieJar;
import com.roxiemobile.networkingapi.network.http.CookieManager;
import com.roxiemobile.networkingapi.network.http.CookiePolicy;
import com.roxiemobile.networkingapi.network.http.CookieStore;
import com.roxiemobile.networkingapi.network.http.HttpHeaders;
import com.roxiemobile.networkingapi.network.http.HttpStatus;
import com.roxiemobile.networkingapi.network.http.InMemoryCookieStore;
import com.roxiemobile.networkingapi.network.http.MediaType;
import com.roxiemobile.networkingapi.network.rest.config.DefaultHttpClientConfig;
import com.roxiemobile.networkingapi.network.rest.config.HttpClientConfig;
import com.roxiemobile.networkingapi.network.rest.request.ByteArrayBody;
import com.roxiemobile.networkingapi.network.rest.request.RequestEntity;
import com.roxiemobile.networkingapi.network.rest.response.BasicResponseEntity;
import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.CertificatePinner;
import okhttp3.CookieJar;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public final class RestApiClient {

// MARK: - Construction

    private RestApiClient(Builder builder) {
        mHttpClientConfig = builder.mHttpClientConfig.clone();

        // Init instance
        mOptions = builder.mOptions.clone();
    }

// MARK: - Methods

    public @NotNull HttpResult get(@NotNull RequestEntity<HttpBody> entity) {
        return execute(MethodName.GET, entity);
    }

    public @NotNull HttpResult post(@NotNull RequestEntity<HttpBody> entity) {
        return execute(MethodName.POST, entity);
    }

    public @NotNull HttpResult put(@NotNull RequestEntity<HttpBody> entity) {
        return execute(MethodName.PUT, entity);
    }

    public @NotNull HttpResult patch(@NotNull RequestEntity<HttpBody> entity) {
        return execute(MethodName.PATCH, entity);
    }

    public @NotNull HttpResult delete(@NotNull RequestEntity<HttpBody> entity) {
        return execute(MethodName.DELETE, entity);
    }

    public @NotNull HttpResult head(@NotNull RequestEntity<HttpBody> entity) {
        return execute(MethodName.HEAD, entity);
    }

    public @NotNull HttpResult options(@NotNull RequestEntity<HttpBody> entity) {
        return execute(MethodName.OPTIONS, entity);
    }

// MARK: - Private Methods

    private HttpResult execute(@NotNull String method, @NotNull RequestEntity<HttpBody> entity) {
        Guard.notEmpty(method, "method is empty");
        Guard.notNull(entity, "entity is null");

        // Execute HTTP request
        return execute(newRequest(method, entity), entity.cookieStore());
    }

    private HttpResult execute(Request request, CookieStore cookieStore) {
        cookieStore = (cookieStore != null) ? cookieStore : new InMemoryCookieStore();
        HttpResult result;

        try {
            try {
                // Create and execute HTTP request
                Response response = newClient(cookieStore).newCall(request).execute();
                result = HttpResult.success(newResponseEntity(response, cookieStore));
            }
            catch (HttpResponseException ex) {
                Logger.e(TAG, ex);

                // Handle interrupted HTTP requests
                result = HttpResult.success(newResponseEntity(ex.getResponse(), cookieStore));
            }
        }
        catch (Exception ex) {
            Logger.e(TAG, ex);

            // Handle any other errors
            result = HttpResult.failure(ex);
        }

        // Done
        return result;
    }

    private @NotNull Request newRequest(@NotNull String method, @NotNull RequestEntity<HttpBody> entity) {
        Request.Builder instance = new Request.Builder();

        // Create request body
        HttpBody entityBody = entity.body();
        RequestBody requestBody = null;

        // NOTE: Workaround for OkHttp crash on POST with empty request body
        if (entityBody == null && method.equals(MethodName.POST)) {
            entityBody = EMPTY_HTTP_BODY;
        }

        if (entityBody != null) {
            okhttp3.MediaType mediaType = okhttp3.MediaType.parse(entityBody.mediaType().toString());
            requestBody = RequestBody.create(mediaType, entityBody.body());
        }

        // Build HTTP request
        HttpHeaders entityHeaders = entity.headers();

        instance.method(method, requestBody)
                .url(entity.uri().toString())
                .headers(mapping(entityHeaders));

        if (requestBody != null) {
            okhttp3.MediaType contentType = requestBody.contentType();

            if (contentType != null) {
                instance.header(HttpHeaders.CONTENT_TYPE, contentType.toString());
            }
        }

        // Done
        return instance.build();
    }

    private @NotNull OkHttpClient newClient(@NotNull CookieStore cookieStore) {
        Guard.notNull(cookieStore, "cookieStore is null");

        CookieManager cookieManager = new CookieManager(cookieStore, CookiePolicy.ACCEPT_ALL);
        CookieJar cookieJar = new CompatJavaNetCookieJar(cookieManager);

        // Retrofit 2.0: The biggest update yet on the best HTTP Client Library for Android
        // @link http://inthecheesefactory.com/blog/retrofit-2.0/en
        // +> Certificate Pinning
        // +> RxJava Integration with CallAdapter

        OkHttpClient.Builder builder = SHARED_HTTP_CLIENT.newBuilder()
                // Set the timeout until a connection is established
                .connectTimeout(mOptions.mConnectionTimeout, TimeUnit.MILLISECONDS)
                // Set the default socket timeout which is the timeout for waiting for data
                .readTimeout(mOptions.mReadTimeout, TimeUnit.MILLISECONDS)
                // Set the handler that can accept cookies from incoming HTTP responses and provides cookies to outgoing HTTP requests
                .cookieJar(cookieJar);

        // Set a application interceptors
        Stream.of(nullToEmpty(mOptions.mInterceptors))
                .filter(Objects::nonNull).forEach(builder::addInterceptor);

        // Set a network interceptors
        Stream.of(nullToEmpty(mOptions.mNetworkInterceptors))
                .filter(Objects::nonNull).forEach(builder::addNetworkInterceptor);

        // Configure secure HTTPS connections
        if (mOptions.mCertificatePinner != null) {
            builder.certificatePinner(mOptions.mCertificatePinner);
        }

        if (mOptions.mHostnameVerifier != null) {
            builder.hostnameVerifier(mOptions.mHostnameVerifier);
        }

        if (mOptions.mSSLSocketFactory != null) {
            if (mOptions.mTrustManager != null) {
                builder.sslSocketFactory(mOptions.mSSLSocketFactory, mOptions.mTrustManager);
            }
            else {
                //noinspection deprecation
                builder.sslSocketFactory(mOptions.mSSLSocketFactory);
            }
        }

        // Done
        return builder.build();
    }

    private @NotNull ResponseEntity<byte[]> newResponseEntity(
            @NotNull Response response,
            @NotNull CookieStore cookieStore
    ) throws IOException {

        Guard.notNull(response, "response is null");
        Guard.notNull(cookieStore, "cookieStore is null");

        // Handle HTTP response
        HttpStatus statusCode = HttpStatus.valueOf(response.code());
        BasicResponseEntity.Builder<byte[]> entityBuilder = new BasicResponseEntity.Builder<byte[]>()
                .uri(response.request().url().uri())
                .headers(mapping(response.headers()))
                .status(statusCode)
                // @see https://tools.ietf.org/html/rfc7231#section-3.1.1.5
                .mediaType(MediaType.APPLICATION_OCTET_STREAM)
                .cookieStore(cookieStore);

        ResponseBody body = response.body();
        if (body != null) {

            // Set MediaType
            okhttp3.MediaType contentType = body.contentType();
            if (contentType != null) {
                entityBuilder.mediaType(MediaType.valueOf(contentType.toString()));
            }

            // Set response body
            entityBuilder.body(ArrayUtils.emptyToNull(body.bytes()));
        }

        // Done
        return entityBuilder.build();
    }

    @Deprecated
    public @NotNull HttpHeaders mapping(Headers headers) {
        HttpHeaders result = new HttpHeaders();

        // Map okhttp3.Headers to HttpHeaders
        if (headers != null && headers.size() > 0) {
            for (String name : headers.names()) {
                result.put(name, headers.values(name));
            }
        }

        // Done
        return result;
    }

    @Deprecated
    public @NotNull Headers mapping(HttpHeaders headers) {
        Headers.Builder builder = new Headers.Builder();

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

    private <T> List<T> nullToEmpty(List<T> list) {
        return (list != null) ? list : Collections.emptyList();
    }

// MARK: - Inner Types

    public static final class Builder {

        public Builder() {
            mOptions = new Options();
        }

        public @NotNull Builder connectionTimeout(long timeout) {
            Guard.isTrue(timeout >= 0, "timeout < 0");
            mOptions.mConnectionTimeout = timeout;
            return this;
        }

        public @NotNull Builder readTimeout(long timeout) {
            Guard.isTrue(timeout >= 0, "timeout < 0");
            mOptions.mReadTimeout = (timeout >= 0) ? timeout : 0;
            return this;
        }

        public @NotNull Builder interceptors(@Nullable List<Interceptor> interceptors) {
            mOptions.mInterceptors = interceptors;
            return this;
        }

        public @NotNull Builder networkInterceptors(@Nullable List<Interceptor> interceptors) {
            mOptions.mNetworkInterceptors = interceptors;
            return this;
        }

        public @NotNull Builder certificatePinner(@Nullable CertificatePinner certificatePinner) {
            mOptions.mCertificatePinner = certificatePinner;
            return this;
        }

        public @NotNull Builder hostnameVerifier(@Nullable HostnameVerifier hostnameVerifier) {
            mOptions.mHostnameVerifier = hostnameVerifier;
            return this;
        }

        public @NotNull Builder sslSocketFactory(@Nullable SSLSocketFactory sslSocketFactory) {
            mOptions.mSSLSocketFactory = sslSocketFactory;
            return this;
        }

        public @NotNull Builder trustManager(@Nullable X509TrustManager trustManager) {
            mOptions.mTrustManager = trustManager;
            return this;
        }

        public @NotNull Builder httpClientConfig(@Nullable HttpClientConfig httpClientConfig) {
            mHttpClientConfig = (httpClientConfig != null) ? httpClientConfig.clone() : DEFAULT_HTTP_CLIENT_CONFIG;
            return this;
        }

        public @NotNull RestApiClient build() {
            return new RestApiClient(this);
        }

        private static final HttpClientConfig DEFAULT_HTTP_CLIENT_CONFIG =
                new DefaultHttpClientConfig();

        private final Options mOptions;

        private HttpClientConfig mHttpClientConfig =
                DEFAULT_HTTP_CLIENT_CONFIG;
    }

    private static final class Options implements Cloneable {

        private Options() {
            // Do nothing
        }

        @SuppressWarnings("MethodDoesntCallSuperMethod")
        @Override
        protected @NotNull Options clone() {
            Options other = new Options();

            // Copy object's state
            other.mConnectionTimeout = mConnectionTimeout;
            other.mReadTimeout = mReadTimeout;
            other.mInterceptors = mInterceptors;
            other.mNetworkInterceptors = mNetworkInterceptors;
            other.mCertificatePinner = mCertificatePinner;
            other.mHostnameVerifier = mHostnameVerifier;
            other.mSSLSocketFactory = mSSLSocketFactory;
            other.mTrustManager = mTrustManager;

            // Done
            return other;
        }

        private long mConnectionTimeout = NetworkConfig.Timeout.INSTANCE.getCONNECTION();
        private long mReadTimeout = NetworkConfig.Timeout.INSTANCE.getREAD();
        private @Nullable List<Interceptor> mInterceptors;
        private @Nullable List<Interceptor> mNetworkInterceptors;
        private CertificatePinner mCertificatePinner;
        private HostnameVerifier mHostnameVerifier;
        private SSLSocketFactory mSSLSocketFactory;
        private X509TrustManager mTrustManager;
    }

    public static class HttpResponseException extends IOException {

        public HttpResponseException(Response response) {
            mResponse = response;
        }

        public Response getResponse() {
            return mResponse;
        }

        private final Response mResponse;
    }

// MARK: - Constants

    private static final String TAG = RestApiClient.class.getSimpleName();

    private static final OkHttpClient SHARED_HTTP_CLIENT = new OkHttpClient.Builder().build();
    private static final HttpBody EMPTY_HTTP_BODY = new ByteArrayBody();

// MARK: - Variables

    private final Options mOptions;

    private final HttpClientConfig mHttpClientConfig;
}
