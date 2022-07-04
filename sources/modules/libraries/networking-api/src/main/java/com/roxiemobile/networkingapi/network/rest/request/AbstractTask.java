package com.roxiemobile.networkingapi.network.rest.request;

import com.roxiemobile.androidcommons.concurrent.ThreadUtils;
import com.roxiemobile.androidcommons.diagnostics.Guard;
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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractTask<Ti extends HttpBody, To>
        implements Task<Ti, To>, Cancellable {

// MARK: - Construction

    protected AbstractTask(@NotNull TaskBuilder<Ti, To> builder) {
        Guard.notNull(builder, "builder is null");

        // Init instance variables
        mTag = builder.tag();
        mRequestEntity = builder.requestEntity();
    }

// MARK: - Properties

    /**
     * The tag associated with a task.
     */
    public final @Nullable String tag() {
        return mTag;
    }

    /**
     * The original request entity.
     */
    public final @NotNull RequestEntity<Ti> requestEntity() {
        return mRequestEntity;
    }

// MARK: - Methods

    /**
     * Synchronously send the request and return its response.
     */
    @Override
    public final void execute(@Nullable Callback<Ti, To> callback) {
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
            result = CallResult.failure(new ApplicationLayerError(ex));
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
    public final @NotNull Cancellable enqueue(@Nullable Callback<Ti, To> callback, boolean callbackOnUiThread) {
        return TaskQueue.enqueue(this, callback, callbackOnUiThread);
    }

    /**
     * Performs the request and returns the response, or throws an exception if unable to do so.
     * May return null if this call was canceled.
     */
    protected final @Nullable CallResult<To> call() throws Exception {
        Guard.isFalse(ThreadUtils.runningOnUiThread(), "This method must not be called from the main thread!");
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
                    result = onSuccess(CallResult.success(entity));
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
                result = onFailure(error);
            }
        }
        else {

            // Handle request cancellation
            onCancel();
        }

        // Done
        return result;
    }

    protected abstract @NotNull HttpResult callExecute();

    /**
     * TODO
     */
    protected final @NotNull RestApiClient createClient() {

        // Get HTTP client config
        HttpClientConfig httpClientConfig = httpClientConfig();

        // Create/init HTTP client
        RestApiClient.Builder builder = new RestApiClient.Builder()
                .httpClientConfig(httpClientConfig);

        // Done
        return builder.build();
    }

    /**
     * TODO
     */
    protected @NotNull HttpClientConfig httpClientConfig() {
        return DEFAULT_HTTP_CLIENT_CONFIG;
    }

    /**
     * TODO
     */
    protected @NotNull RequestEntity<HttpBody> createRequestEntity(@NotNull HttpRoute route) {
        // Create HTTP request entity
        return new BasicRequestEntity.Builder<>(requestEntity(), httpBody())
                .uri(route.toURI())
                .headers(httpHeaders())
                .build();
    }

    /**
     * TODO
     */
    protected @NotNull HttpHeaders httpHeaders() {
        return HttpHeaders.readOnlyHttpHeaders(requestEntity().headers());
    }

    /**
     * TODO
     */
    protected @Nullable HttpBody httpBody() {
        return requestEntity().body();
    }

    /**
     * TODO
     */
    protected abstract @NotNull CallResult<To> onSuccess(@NotNull CallResult<byte[]> httpResult);

    /**
     * TODO
     */
    protected @NotNull CallResult<To> onFailure(@NotNull RestApiError error) {
        Guard.notNull(error, "error is null");
        return CallResult.failure(error);
    }

    /**
     * TODO
     */
    protected void onCancel() {
        // Do nothing
    }

    /**
     * TODO
     */
    @Override
    public final @NotNull Task<Ti, To> clone() {
        return createBuilder().build();
    }

    /**
     * TODO
     */
    protected abstract @NotNull TaskBuilder<Ti, To> createBuilder();

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

    private void yield(@Nullable CallResult<To> result, @NotNull Callback<Ti, To> callback) {
        Guard.notNull(callback, "callback is null");

        if (isCancelled()) {
            callback.onCancel(this);
        }
        else {
            if (result != null) {
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
    }

// MARK: - Inner Types

    public abstract static class Builder<Ti, To, BuilderType extends TaskBuilder<Ti, To>>
            implements TaskBuilder<Ti, To> {

        public Builder() {
            // Do nothing
        }

        protected Builder(@NotNull Task<Ti, To> task) {
            // Init instance variables
            mTag = task.tag();
            mRequestEntity = task.requestEntity();
        }

        public @Nullable String tag() {
            return mTag;
        }

        public @NotNull BuilderType tag(@Nullable String tag) {
            mTag = tag;
            //noinspection unchecked
            return (BuilderType) this;
        }

        public @Nullable RequestEntity<Ti> requestEntity() {
            return mRequestEntity;
        }

        public @NotNull BuilderType requestEntity(@NotNull RequestEntity<Ti> request) {
            mRequestEntity = request;
            //noinspection unchecked
            return (BuilderType) this;
        }

        public @NotNull Task<Ti, To> build() {
            checkInvalidState();
            return createTask();
        }

        protected void checkInvalidState() {
            Guard.notNull(mRequestEntity, "requestEntity is null");
            Guard.notNull(mRequestEntity.uri(), "requestEntity.uri is null");
        }

        protected abstract @NotNull Task<Ti, To> createTask();

        private @Nullable String mTag;
        private @Nullable RequestEntity<Ti> mRequestEntity;
    }

// MARK: - Constants

    private static final @NotNull HttpClientConfig DEFAULT_HTTP_CLIENT_CONFIG =
            new DefaultHttpClientConfig();

// MARK: - Variables

    private final @Nullable String mTag;

    private final @NotNull RequestEntity<Ti> mRequestEntity;

    private final @NotNull AtomicBoolean mCancelled = new AtomicBoolean(false);
}
