package com.roxiemobile.networkingapi.network.rest;

import android.support.annotation.NonNull;
import android.util.Log;

import com.roxiemobile.androidcommons.util.ArrayUtils;
import com.roxiemobile.androidcommons.util.LogUtils;
import com.roxiemobile.androidcommons.util.StringUtils;
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
import com.roxiemobile.networkingapi.network.rest.interceptor.RedirectInterceptor;
import com.roxiemobile.networkingapi.network.rest.request.ByteArrayBody;
import com.roxiemobile.networkingapi.network.rest.request.RequestEntity;
import com.roxiemobile.networkingapi.network.rest.response.BasicResponseEntity;
import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity;

import java.io.IOException;
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
import okhttp3.internal.Version;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;

import static com.roxiemobile.androidcommons.util.AssertUtils.assertFalse;
import static com.roxiemobile.androidcommons.util.AssertUtils.assertNotNull;
import static com.roxiemobile.androidcommons.util.AssertUtils.assertTrue;

public final class RestApiClient
{
// MARK: - Construction

    private RestApiClient(Builder builder) {
        // Init instance variables
        mOptions = builder.mOptions.clone();
    }

// MARK: - Methods

    public @NonNull HttpResult get(@NonNull RequestEntity<HttpBody> entity) {
        return execute(MethodName.GET, entity);
    }

    public @NonNull HttpResult post(@NonNull RequestEntity<HttpBody> entity) {
        return execute(MethodName.POST, entity);
    }

    public @NonNull HttpResult put(@NonNull RequestEntity<HttpBody> entity) {
        return execute(MethodName.PUT, entity);
    }

    public @NonNull HttpResult patch(@NonNull RequestEntity<HttpBody> entity) {
        return execute(MethodName.PATCH, entity);
    }

    public @NonNull HttpResult delete(@NonNull RequestEntity<HttpBody> entity) {
        return execute(MethodName.DELETE, entity);
    }

    public @NonNull HttpResult head(@NonNull RequestEntity<HttpBody> entity) {
        return execute(MethodName.HEAD, entity);
    }

    public @NonNull HttpResult options(@NonNull RequestEntity<HttpBody> entity) {
        return execute(MethodName.OPTIONS, entity);
    }

// MARK: - Private Methods

    private HttpResult execute(@NonNull String method, @NonNull RequestEntity<HttpBody> entity) {
        assertFalse(StringUtils.isEmpty(method), "method is empty");
        assertNotNull(entity, "entity == null");

        // Execute HTTP request
        return execute(newRequest(method, entity), entity.cookieStore());
    }

    private HttpResult execute(Request request, CookieStore cookieStore) {
        cookieStore = (cookieStore != null) ? cookieStore : new InMemoryCookieStore();
        HttpResult result;

        try {
            // Create and execute HTTP request
            Response response = newClient(cookieStore).newCall(request).execute();
            result = HttpResult.success(newResponseEntity(response, cookieStore));
        }
        catch (HttpResponseException ex) {
            LogUtils.e(TAG, ex);

            // Handle interrupted HTTP requests
            result = HttpResult.success(newResponseEntity(ex.getResponse(), cookieStore));
        }
        catch (Exception ex) {
            LogUtils.e(TAG, ex);

            // Handle any other errors
            result = HttpResult.failure(ex);
        }

        // Done
        return result;
    }

    private @NonNull Request newRequest(@NonNull String method, @NonNull RequestEntity<HttpBody> entity) {
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
                .headers(mapping(entityHeaders))
                .header(HttpHeaders.USER_AGENT, newUserAgent(entityHeaders));

        if (requestBody != null) {
            instance.header(HttpHeaders.CONTENT_TYPE, requestBody.contentType().toString());
        }

        // Done
        return instance.build();
    }

    private String newUserAgent(HttpHeaders headers) {
        return (StringUtils.nullToEmpty(headers.getUserAgent()).trim() + " " + Version.userAgent()).trim();
    }

    private @NonNull OkHttpClient newClient(@NonNull CookieStore cookieStore) {
        assertNotNull(cookieStore, "cookieStore == null");

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

        // Set an interceptor which handles HTTP redirects
        Interceptor interceptor = mOptions.mRedirectInterceptor;
        if (interceptor != null) {
            builder.addNetworkInterceptor(interceptor);
        }

        // Set an interceptor which logs request and response information
        if (LogUtils.isLoggable(Log.DEBUG)) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();

            // Log request and response lines and their respective headers and bodies (if present).
            logging.setLevel(Level.BODY);
            builder.addNetworkInterceptor(logging);
        }

        // Done
        return builder.build();
    }

    private @NonNull ResponseEntity<byte[]> newResponseEntity(@NonNull Response response, @NonNull CookieStore cookieStore) {
        assertNotNull(response, "response == null");
        assertNotNull(cookieStore, "cookieStore == null");

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

            try {
                // Set response body
                entityBuilder.body(ArrayUtils.emptyToNull(body.bytes()));
            }
            catch (Exception ex) {
                LogUtils.w(TAG, ex);
            }
        }

        // Done
        return entityBuilder.build();
    }

    @Deprecated
    public @NonNull HttpHeaders mapping(Headers headers) {
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
    public @NonNull Headers mapping(HttpHeaders headers) {
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

// MARK: - Inner Types

    public static final class Builder
    {
        public Builder() {
            mOptions = new Options();
        }

        public Builder connectTimeout(int timeout) {
            assertTrue(timeout >= 0, "timeout < 0");
            mOptions.mConnectionTimeout = timeout;
            return this;
        }

        public Builder readTimeout(int timeout) {
            assertTrue(timeout >= 0, "timeout < 0");
            mOptions.mReadTimeout = (timeout >= 0) ? timeout : 0;
            return this;
        }

        public Builder redirectInterceptor(RedirectInterceptor interceptor) {
            mOptions.mRedirectInterceptor = interceptor;
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
            other.mRedirectInterceptor = mRedirectInterceptor;

            // Done
            return other;
        }

        private int mConnectionTimeout = NetworkConfig.Timeout.CONNECTION;
        private int mReadTimeout = NetworkConfig.Timeout.READ;
        private RedirectInterceptor mRedirectInterceptor;
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

    private static final OkHttpClient SHARED_HTTP_CLIENT = new OkHttpClient.Builder().build();
    private static final HttpBody EMPTY_HTTP_BODY = new ByteArrayBody();

// MARK: - Variables

    private Options mOptions;

}
