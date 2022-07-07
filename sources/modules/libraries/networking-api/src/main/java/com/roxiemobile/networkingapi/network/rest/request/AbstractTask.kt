@file:Suppress("DEPRECATION", "MemberVisibilityCanBePrivate", "UNCHECKED_CAST", "unused")

package com.roxiemobile.networkingapi.network.rest.request

import com.roxiemobile.androidcommons.concurrent.ThreadUtils
import com.roxiemobile.networkingapi.network.http.HttpHeaders
import com.roxiemobile.networkingapi.network.http.body.HttpBody
import com.roxiemobile.networkingapi.network.rest.CallResult
import com.roxiemobile.networkingapi.network.rest.Callback
import com.roxiemobile.networkingapi.network.rest.Cancellable
import com.roxiemobile.networkingapi.network.rest.HttpResult
import com.roxiemobile.networkingapi.network.rest.RestApiClient
import com.roxiemobile.networkingapi.network.rest.Task
import com.roxiemobile.networkingapi.network.rest.TaskQueue
import com.roxiemobile.networkingapi.network.rest.configuration.DefaultHttpClientConfig
import com.roxiemobile.networkingapi.network.rest.configuration.HttpClientConfig
import com.roxiemobile.networkingapi.network.rest.response.RestApiError
import com.roxiemobile.networkingapi.network.rest.response.error.ApplicationLayerError
import com.roxiemobile.networkingapi.network.rest.response.error.TransportLayerError
import com.roxiemobile.networkingapi.network.rest.response.error.nested.ConnectionException
import com.roxiemobile.networkingapi.network.rest.response.error.nested.ResponseException
import com.roxiemobile.networkingapi.network.rest.routing.HttpRoute
import java.io.IOException
import java.util.concurrent.atomic.AtomicBoolean

abstract class AbstractTask<Ti: HttpBody, To>:
    Task<Ti, To>,
    Cancellable {

// MARK: - Construction

    protected constructor(builder: TaskBuilder<Ti, To>) {
        _tag = builder.tag
        _requestEntity = requireNotNull(builder.requestEntity) { "requestEntity is null" }
    }

// MARK: - Properties

    /**
     * The tag associated with a task.
     */
    final override val tag: String?
        get() = _tag

    /**
     * The original request entity.
     */
    final override val requestEntity: RequestEntity<Ti>
        get() = _requestEntity

// MARK: - Methods

    /**
     * Synchronously send the request and return its response.
     */
    final override fun execute(callback: Callback<Ti, To>?) {
        var shouldExecute = true

        var callResult: CallResult<To>? = null
        try {

            // Check if task must be executed
            if (callback != null) {
                shouldExecute = callback.onShouldExecute(this)
            }

            // Execute task if needed
            if (shouldExecute) {
                callResult = call()
            }
        }
        catch (ex: Throwable) {
            callResult = CallResult.failure(ApplicationLayerError(ex))
        }

        // Yielding result to listener
        if (callback != null && shouldExecute) {
            yieldTo(callback, callResult)
        }
    }

    /**
     * TODO
     */
    final override fun enqueue(callback: Callback<Ti, To>?, callbackOnUiThread: Boolean): Cancellable {
        return TaskQueue.enqueue(this, callback, callbackOnUiThread)
    }

    /**
     * Performs the request and returns the response, or throws an exception if unable
     * to do so. May return `null` if this call was canceled.
     */
    protected fun call(): CallResult<To>? {
        check(!ThreadUtils.runningOnUiThread()) { "This method must not be called from the main thread!" }

        var callResult: CallResult<To>? = null
        var error: RestApiError? = null

        // Send request to the server
        val httpResult: HttpResult = callExecute()

        // Are HTTP response is still needed?
        if (!isCancelled()) {

            // Handle HTTP response
            if (httpResult.isSuccess) {

                val responseEntity = checkNotNull(httpResult.value()) { "httpResult.value() is null" }
                val httpStatus = responseEntity.httpStatus

                // Create a new call result
                if (httpStatus.is2xxSuccessful) {
                    callResult = onSuccess(CallResult.success(responseEntity))
                }
                else {
                    val cause = ResponseException(responseEntity)
                    // Build application layer error
                    error = ApplicationLayerError(cause)
                }
            }
            else {
                var cause = checkNotNull(httpResult.error()) { "httpResult.error() is null" }

                // Wrap up HTTP connection error
                if (cause is IOException) {
                    cause = ConnectionException(cause)
                }

                // Build transport layer error
                error = TransportLayerError(cause)
            }

            // Handle error
            if (error != null) {
                callResult = onFailure(error)
            }
        }
        else {

            // Handle request cancellation
            onCancel()
        }

        // Done
        return callResult
    }

    /**
     * TODO
     */
    protected abstract fun callExecute(): HttpResult

    /**
     * TODO
     */
    protected fun createClient(): RestApiClient {

        // Get HTTP client config
        val httpClientConfig = httpClientConfig()

        // Create/init HTTP client
        return RestApiClient.Builder()
            .httpClientConfig(httpClientConfig)
            .build()
    }

    /**
     * TODO
     */
    protected open fun httpClientConfig(): HttpClientConfig {
        return DefaultHttpClientConfig.SHARED
    }

    /**
     * TODO
     */
    protected open fun createRequestEntity(httpRoute: HttpRoute): RequestEntity<HttpBody> {

        // Create HTTP request entity
        return BasicRequestEntity.Builder
            .of(_requestEntity, httpBody())
            .link(httpRoute.link)
            .httpHeaders(httpHeaders())
            .build()
    }

    /**
     * TODO
     */
    protected open fun httpHeaders(): HttpHeaders {
        return HttpHeaders.readOnlyHttpHeaders(_requestEntity.httpHeaders)
    }

    /**
     * TODO
     */
    protected open fun httpBody(): HttpBody? {
        return _requestEntity.body
    }

    /**
     *
     * TODO
     */
    protected abstract fun onSuccess(callResult: CallResult<ByteArray>): CallResult<To>

    /**
     * TODO
     */
    protected open fun onFailure(error: RestApiError): CallResult<To> {
        return CallResult.failure(error)
    }

    /**
     * TODO
     */
    protected open fun onCancel() {
        // Do nothing
    }

    /**
     * TODO
     */
    final override fun clone(): Task<Ti, To> {
        return createBuilder().build()
    }

    /**
     * TODO
     */
    protected abstract fun createBuilder(): TaskBuilder<Ti, To>

    /**
     * TODO
     */
    final override fun cancel(): Boolean {
        return !_cancelled.getAndSet(true)
    }

    /**
     * TODO
     */
    fun isCancelled(): Boolean {
        return _cancelled.get()
    }

// MARK: - Private Methods

    private fun yieldTo(callback: Callback<Ti, To>, callResult: CallResult<To>?) {

        if (isCancelled()) {
            callback.onCancel(this)
        }
        else {
            if (callResult != null) {
                if (callResult.isSuccess) {
                    val value = checkNotNull(callResult.value()) { "value is null" }
                    callback.onSuccess(this, value)
                }
                else {
                    val error = checkNotNull(callResult.error()) { "error is null" }
                    callback.onFailure(this, error)
                }
            }
            else {
                error("!isCancelled() && (callResult == null)")
            }
        }
    }

// MARK: - Inner Types

    abstract class Builder<Ti, To, BuilderType: TaskBuilder<Ti, To>>(): TaskBuilder<Ti, To> {

        protected constructor(task: Task<Ti, To>): this() {
            _tag = task.tag
            _requestEntity = task.requestEntity
        }

        override val tag: String?
            get() = _tag

        override val requestEntity: RequestEntity<Ti>?
            get() = _requestEntity

        fun tag(tag: String?): BuilderType {
            _tag = tag
            return this as BuilderType
        }

        fun requestEntity(requestEntity: RequestEntity<Ti>): BuilderType {
            _requestEntity = requestEntity
            return this as BuilderType
        }

        override fun build(): Task<Ti, To> {
            return createTask()
        }

        protected abstract fun createTask(): Task<Ti, To>

        private var _tag: String? = null
        private var _requestEntity: RequestEntity<Ti>? = null
    }

// MARK: - Variables

    private val _tag: String?

    private val _requestEntity: RequestEntity<Ti>

    private val _cancelled = AtomicBoolean(false)
}
