package com.roxiemobile.networkingapi.network.rest;

import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;

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
import com.roxiemobile.networkingapi.network.rest.request.ByteArrayBody;
import com.roxiemobile.networkingapi.network.rest.request.RequestEntity;
import com.roxiemobile.networkingapi.network.rest.response.BasicResponseEntity;
import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity;
import com.roxiemobile.networkingapi.network.security.TLSCompat;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import okhttp3.CookieJar;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public final class RestApiClient
{
// MARK: - Construction

    private RestApiClient(Builder builder) {
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

    private static @NotNull OkHttpClient newSharedHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        if (VERSION.SDK_INT < VERSION_CODES.LOLLIPOP) {
            TLSCompat.enableTlsOnSockets(builder);
        }
        return builder.build();
    }

// MARK: - Private Methods

    private <T> List<T> nullToEmpty(List<T> list) {
        return (list != null) ? list : Collections.emptyList();
    }

// MARK: - Inner Types

    public static final class Builder
    {
        public Builder() {
            mOptions = new Options();
        }

        public Builder connectTimeout(int timeout) {
            Guard.isTrue(timeout >= 0, "timeout < 0");
            mOptions.mConnectionTimeout = timeout;
            return this;
        }

        public Builder readTimeout(int timeout) {
            Guard.isTrue(timeout >= 0, "timeout < 0");
            mOptions.mReadTimeout = (timeout >= 0) ? timeout : 0;
            return this;
        }

        public Builder interceptors(List<Interceptor> interceptors) {
            mOptions.mInterceptors = interceptors;
            return this;
        }

        public Builder networkInterceptors(List<Interceptor> interceptors) {
            mOptions.mNetworkInterceptors = interceptors;
            return this;
        }

        public RestApiClient build() {
            return new RestApiClient(this);
        }

        private final Options mOptions;
    }

    private static final class Options implements Cloneable
    {
        private Options() {
            // Do nothing
        }

        @SuppressWarnings("CloneDoesntCallSuperClone")
        @Override
        protected Options clone() {
            Options other = new Options();

            // Copy object's state
            other.mConnectionTimeout = mConnectionTimeout;
            other.mReadTimeout = mReadTimeout;
            other.mInterceptors = mInterceptors;
            other.mNetworkInterceptors = mNetworkInterceptors;

            // Done
            return other;
        }

        private int mConnectionTimeout = NetworkConfig.Timeout.CONNECTION;
        private int mReadTimeout = NetworkConfig.Timeout.READ;
        private List<Interceptor> mInterceptors;
        private List<Interceptor> mNetworkInterceptors;
    }

    public static class HttpResponseException extends IOException
    {
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

    private static final OkHttpClient SHARED_HTTP_CLIENT = newSharedHttpClient();
    private static final HttpBody EMPTY_HTTP_BODY = new ByteArrayBody();

// MARK: - Variables

    private Options mOptions;
}