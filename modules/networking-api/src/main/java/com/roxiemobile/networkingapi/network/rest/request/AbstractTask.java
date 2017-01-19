package com.roxiemobile.networkingapi.network.rest.request;

import android.support.annotation.NonNull;

import com.roxiemobile.androidcommons.concurrent.ThreadUtils;
import com.roxiemobile.networkingapi.network.http.HttpHeaders;
import com.roxiemobile.networkingapi.network.http.HttpStatus;
import com.roxiemobile.networkingapi.network.rest.CallResult;
import com.roxiemobile.networkingapi.network.rest.Callback;
import com.roxiemobile.networkingapi.network.rest.Cancellable;
import com.roxiemobile.networkingapi.network.rest.HttpBody;
import com.roxiemobile.networkingapi.network.rest.HttpResult;
import com.roxiemobile.networkingapi.network.rest.RestApiClient;
import com.roxiemobile.networkingapi.network.rest.Task;
import com.roxiemobile.networkingapi.network.rest.TaskQueue;
import com.roxiemobile.networkingapi.network.rest.config.DefaultHttpClientConfig;
import com.roxiemobile.networkingapi.network.rest.config.HttpClientConfig;
import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity;
import com.roxiemobile.networkingapi.network.rest.response.RestApiError;
import com.roxiemobile.networkingapi.network.rest.response.error.ApplicationLayerError;
import com.roxiemobile.networkingapi.network.rest.response.error.TransportLayerError;
import com.roxiemobile.networkingapi.network.rest.response.error.nested.ConnectionException;
import com.roxiemobile.networkingapi.network.rest.response.error.nested.ResponseException;
import com.roxiemobile.networkingapi.network.rest.routing.HttpRoute;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.roxiemobile.androidcommons.diagnostics.Require.requireFalse;
import static com.roxiemobile.androidcommons.diagnostics.Require.requireNotNull;

public abstract class AbstractTask<Ti extends HttpBody, To> implements Task<Ti, To>, Cancellable
{
// MARK: - Construction

    protected AbstractTask(@NonNull TaskBuilder<Ti, To> builder) {
        requireNotNull(builder, "builder is null");

        // Init instance variables
        mTag = builder.tag();
        mRequestEntity = builder.requestEntity();
    }

// MARK: - Properties

    /**
     * The tag associated with a task.
     */
    public final String tag() {
        return mTag;
    }

    /**
     * The original request entity.
     */
    public final @NonNull RequestEntity<Ti> requestEntity() {
        return mRequestEntity;
    }

// MARK: - Methods

    /**
     * Synchronously send the request and return its response.
     */
    @Override
    public final void execute(Callback<Ti, To> callback) {
        boolean shouldExecute = true;

        CallResult<To> result = null;
        try {

            // Check if task must be executed
            if (callback != null) {
                shouldExecute = callback.onShouldExecute(this);
            }

            // Execute task if needed
            if (shouldExecute) {
                result = call();
            }
        }
        catch (Throwable ex) {
            result = CallResult.<To>failure(new ApplicationLayerError(ex));
        }

        // Yielding result to listener
        if (callback != null && shouldExecute) {
            yield(result, callback);
        }
    }

    /**
     * TODO
     */
    @Override
    public final Cancellable enqueue(Callback<Ti, To> callback, boolean callbackOnUiThread) {
        return TaskQueue.enqueue(this, callback, callbackOnUiThread);
    }

    /**
     * Performs the request and returns the response, or throws an exception if unable to do so.
     * May return null if this call was canceled.
     */
    protected final CallResult<To> call() throws Exception {
        requireFalse(ThreadUtils.runningOnUiThread(), "This method must not be called from the main thread!");
        CallResult<To> result = null;

        // Send request to the server
        HttpResult httpResult = callExecute();
        RestApiError error = null;

        // Are HTTP response is still needed?
        if (!isCancelled()) {

            // Handle HTTP response
            if (httpResult.isSuccess()) {

                ResponseEntity<byte[]> entity = httpResult.value();
                HttpStatus status = entity.status();

                // Create a new call result
                if (status.is2xxSuccessful()) {
                    result = onResult(CallResult.success(entity));
                }
                else {
                    ResponseException cause = new ResponseException(entity);
                    // Build application layer error
                    error = new ApplicationLayerError(cause);
                }
            }
            else {
                Throwable cause = httpResult.error();

                // Wrap up HTTP connection error
                if (cause instanceof IOException) {
                    cause = new ConnectionException(cause);
                }

                // Build transport layer error
                error = new TransportLayerError(cause);
            }

            // Handle error
            if (error != null) {
                result = CallResult.<To>failure(error);
            }
        }

        // Done
        return result;
    }

    protected abstract HttpResult callExecute();

    /**
     * TODO
     */
    protected final @NonNull RestApiClient newClient() {
        // Get HTTP client config
        HttpClientConfig config = httpClientConfig();
        requireNotNull(config, "config is null");

        // Create/init HTTP client
        RestApiClient.Builder builder = new RestApiClient.Builder()
                // Set the timeout until a connection is established
                .connectTimeout(config.connectTimeout())
                // Set the default socket timeout which is the timeout for waiting for data
                .readTimeout(config.readTimeout())
                // Set an application interceptors
                .interceptors(config.interceptors())
                // Set an network interceptors
                .networkInterceptors(config.networkInterceptors());

        // Done
        return builder.build();
    }

    /**
     * TODO
     */
    protected @NonNull HttpClientConfig httpClientConfig() {
        return DEFAULT_HTTP_CLIENT_CONFIG;
    }

    /**
     * TODO
     */
    protected @NonNull RequestEntity<HttpBody> newRequestEntity(@NonNull HttpRoute route) {
        // Create HTTP request entity
        return new BasicRequestEntity.Builder<>(requestEntity(), httpBody())
                        .uri(route.toURI())
                        .headers(httpHeaders())
                        .build();
    }

    /**
     * TODO
     */
    protected @NonNull HttpHeaders httpHeaders() {
        return HttpHeaders.readOnlyHttpHeaders(requestEntity().headers());
    }

    /**
     * TODO
     */
    protected @NonNull HttpBody httpBody() {
        return requestEntity().body();
    }

    /**
     * TODO
     */
    protected abstract CallResult<To> onResult(CallResult<byte[]> httpResult);

    /**
     * TODO
     */
    @SuppressWarnings("CloneDoesntCallSuperClone")
    @Override
    public final Task<Ti, To> clone() {
        return newBuilder().build();
    }

    /**
     * TODO
     */
    protected abstract TaskBuilder<Ti, To> newBuilder();

    /**
     * TODO
     */
    public final boolean cancel() {
        return !mCancelled.getAndSet(true);
    }

    /**
     * TODO
     */
    public final boolean isCancelled() {
        return mCancelled.get();
    }

// MARK: - Private Methods

    private void yield(CallResult<To> result, @NonNull Callback<Ti, To> callback) {
        requireNotNull(callback, "callback is null");

        if (!isCancelled())
        {
            if (result != null)
            {
                if (result.isSuccess()) {
                    callback.onSuccess(this, result.value());
                }
                else {
                    callback.onFailure(this, result.error());
                }
            }
            else {
                throw new IllegalStateException("!isCancelled() && (result == null)");
            }
        }
        else {
            callback.onCancel(this);
        }
    }

// MARK: - Inner Types

    public abstract static class Builder<Ti, To, BuilderType extends TaskBuilder<Ti, To>>
            implements TaskBuilder<Ti, To>
    {
        public Builder() {
            // Do nothing
        }

        protected Builder(@NonNull Task<Ti, To> task) {
            // Init instance variables
            mTag = task.tag();
            mRequestEntity = task.requestEntity();
        }

        public String tag() {
            return mTag;
        }

        public @NonNull BuilderType tag(String tag) {
            mTag = tag;
            //noinspection unchecked
            return (BuilderType) this;
        }

        public RequestEntity<Ti> requestEntity() {
            return mRequestEntity;
        }

        public @NonNull BuilderType requestEntity(RequestEntity<Ti> request) {
            mRequestEntity = request;
            //noinspection unchecked
            return (BuilderType) this;
        }

        public @NonNull Task<Ti, To> build() {
            checkInvalidState();
            return newTask();
        }

        protected void checkInvalidState() {
            requireNotNull(mRequestEntity, "requestEntity is null");
            requireNotNull(mRequestEntity.uri(), "requestEntity.uri is null");
        }

        protected abstract @NonNull Task<Ti, To> newTask();

        private String mTag;
        private RequestEntity<Ti> mRequestEntity;
    }

// MARK: - Constants

    private static final HttpClientConfig DEFAULT_HTTP_CLIENT_CONFIG =
            new DefaultHttpClientConfig();

// MARK: - Variables

    private final String mTag;

    private final RequestEntity<Ti> mRequestEntity;

    private final AtomicBoolean mCancelled = new AtomicBoolean(false);

}
