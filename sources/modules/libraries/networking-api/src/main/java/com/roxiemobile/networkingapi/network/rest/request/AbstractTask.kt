@file:Suppress("DEPRECATION", "UNCHECKED_CAST", "unused")

package com.roxiemobile.networkingapi.network.rest.request

import com.roxiemobile.androidcommons.concurrent.ThreadUtils
import com.roxiemobile.java.util.toBase62
import com.roxiemobile.networkingapi.network.http.CookieStore
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
import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity
import com.roxiemobile.networkingapi.network.rest.response.RestApiError
import com.roxiemobile.networkingapi.network.rest.response.error.ApplicationLayerError
import com.roxiemobile.networkingapi.network.rest.response.error.TransportLayerError
import com.roxiemobile.networkingapi.network.rest.response.error.nested.ConnectionException
import com.roxiemobile.networkingapi.network.rest.response.error.nested.ResponseException
import java.io.IOException
import java.net.URI
import java.util.UUID
import java.util.concurrent.CancellationException
import java.util.concurrent.atomic.AtomicBoolean

abstract class AbstractTask<Ti: HttpBody, To>:
    Task<Ti, To>,
    Cancellable {

// MARK: - Construction

    protected constructor(builder: Builder<Ti, To, *>) {
        this.tag = builder.tag ?: createTag()
        this.requestEntity = requireNotNull(builder.requestEntity) { "requestEntity is null" }
        this.httpClientConfig = builder.httpClientConfig ?: DefaultHttpClientConfig.SHARED
    }

// MARK: - Properties

    /**
     * The tag associated with a task.
     */
    final override val tag: String

    /**
     * The original request entity.
     */
    final override val requestEntity: RequestEntity<Ti>

    /**
     * TODO
     */
    val httpClientConfig: HttpClientConfig

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
    protected fun call(): CallResult<To> {
        check(!ThreadUtils.runningOnUiThread()) { "This method must not be called from the main thread!" }

        val httpResult: HttpResult = callExecute()
        val callResult: CallResult<To>

        if (_cancelled.get()) {
            callResult = onCancelled()
        }
        else {
            callResult = httpResult.fold(
                onSuccess = { responseEntity ->

                    val httpStatus = responseEntity.httpStatus

                    if (httpStatus.is1xxInformational || httpStatus.is2xxSuccessful) {
                        onSucceeded(responseEntity)
                    }
                    else {
                        val cause = ResponseException(responseEntity)
                        onFailed(ApplicationLayerError(cause))
                    }
                },
                onFailure = { error ->

                    val cause = when (error) {
                        // Wrap the HTTP connection error
                        is IOException -> ConnectionException(error)
                        else -> error
                    }

                    when (cause) {
                        is CancellationException -> onCancelled()
                        else -> onFailed(TransportLayerError(cause))
                    }
                },
            )
        }

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

        // Create HTTP client
        return RestApiClient.Builder()
            .httpClientConfig(httpClientConfig())
            .build()
    }

    /**
     * TODO
     */
    protected open fun httpClientConfig(): HttpClientConfig {
        return this.httpClientConfig
    }

    /**
     * TODO
     */
    protected fun createRequestEntity(): RequestEntity<HttpBody> {

        // Create HTTP request entity
        return BasicRequestEntity.Builder<HttpBody>()
            .link(link())
            .httpHeaders(httpHeaders())
            .cookieStore(cookieStore())
            .body(httpBody())
            .build()
    }

    /**
     * TODO
     */
    protected open fun link(): URI {
        return this.requestEntity.link
    }

    /**
     * TODO
     */
    protected open fun httpHeaders(): HttpHeaders {
        return HttpHeaders().also {
            it.putAll(this.requestEntity.httpHeaders)
        }
    }

    /**
     * TODO
     */
    protected open fun cookieStore(): CookieStore {
        return this.requestEntity.cookieStore
    }

    /**
     * TODO
     */
    protected open fun httpBody(): HttpBody? {
        return this.requestEntity.body
    }

    /**
     * TODO
     */
    protected abstract fun onSucceeded(responseEntity: ResponseEntity<ByteArray>): CallResult<To>

    /**
     * TODO
     */
    protected abstract fun onFailed(restApiError: RestApiError): CallResult<To>

    /**
     * TODO
     */
    protected abstract fun onCancelled(): CallResult<To>

    /**
     * TODO
     */
    final override fun clone(): Task<Ti, To> {
        return createBuilder().build()
    }

    /**
     * TODO
     */
    abstract fun createBuilder(): TaskBuilder<Ti, To>

    /**
     * TODO
     */
    final override fun cancel(): Boolean {
        return !_cancelled.getAndSet(true)
    }

// MARK: - Private Methods

    private fun yieldTo(callback: Callback<Ti, To>, callResult: CallResult<To>?) {

        if (_cancelled.get()) {
            callback.onCancelled(this)
        }
        else if (callResult != null) {
            if (callResult.isSuccess) {
                val value = checkNotNull(callResult.value()) { "value is null" }
                callback.onSucceeded(this, value)
            }
            else {
                val exception = checkNotNull(callResult.error()) { "exception is null" }
                when (exception) {
                    is CancellationException -> callback.onCancelled(this)
                    is RestApiError -> callback.onFailed(this, exception)
                    else -> error("Unsupported exception: ${exception.javaClass.name}")
                }
            }
        }
        else {
            error("!isCancelled() && (callResult == null)")
        }
    }

// MARK: - Inner Types

    abstract class Builder<Ti: HttpBody, To, BuilderType: TaskBuilder<Ti, To>>:
        TaskBuilder<Ti, To> {

        protected constructor()

        protected constructor(builder: Builder<Ti, To, BuilderType>) {
            this.tag = builder.tag
            this.requestEntity = builder.requestEntity
            this.httpClientConfig = builder.httpClientConfig
        }

        protected constructor(task: AbstractTask<Ti, To>) {
            this.tag = task.tag
            this.requestEntity = task.requestEntity
            this.httpClientConfig = task.httpClientConfig
        }

        internal var tag: String? = null
            private set

        internal var requestEntity: RequestEntity<Ti>? = null
            private set

        internal var httpClientConfig: HttpClientConfig? = null
            private set

        override fun tag(tag: String?): BuilderType {
            this.tag = tag
            return this as BuilderType
        }

        override fun requestEntity(requestEntity: RequestEntity<Ti>): BuilderType {
            this.requestEntity = requestEntity.clone()
            return this as BuilderType
        }

        override fun httpClientConfig(httpClientConfig: HttpClientConfig): BuilderType {
            this.httpClientConfig = httpClientConfig.clone()
            return this as BuilderType
        }

        abstract override fun build(): AbstractTask<Ti, To>

        abstract override fun clone(): Builder<Ti, To, BuilderType>
    }

// MARK: - Companion

    companion object {

        private fun createTag(): String {
            return "urn:tag:" + UUID.randomUUID().toBase62()
        }
    }

// MARK: - Variables

    private val _cancelled = AtomicBoolean(false)
}
