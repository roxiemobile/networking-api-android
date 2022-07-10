@file:Suppress("DEPRECATION", "unused")

package com.roxiemobile.networkingapi.network.rest

import com.roxiemobile.androidcommons.logging.Logger
import com.roxiemobile.androidcommons.util.ArrayUtils
import com.roxiemobile.networkingapi.network.http.CompatJavaNetCookieJar
import com.roxiemobile.networkingapi.network.http.CookieManager
import com.roxiemobile.networkingapi.network.http.CookiePolicy
import com.roxiemobile.networkingapi.network.http.CookieStore
import com.roxiemobile.networkingapi.network.http.HttpHeaders
import com.roxiemobile.networkingapi.network.http.HttpMethod
import com.roxiemobile.networkingapi.network.http.HttpStatus
import com.roxiemobile.networkingapi.network.http.MediaType
import com.roxiemobile.networkingapi.network.http.body.ByteArrayBody
import com.roxiemobile.networkingapi.network.http.body.HttpBody
import com.roxiemobile.networkingapi.network.rest.configuration.DefaultHttpClientConfig
import com.roxiemobile.networkingapi.network.rest.configuration.DefaultRequestTimeoutConfig
import com.roxiemobile.networkingapi.network.rest.configuration.HttpClientConfig
import com.roxiemobile.networkingapi.network.rest.request.RequestEntity
import com.roxiemobile.networkingapi.network.rest.response.BasicResponseEntity
import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity
import okhttp3.CookieJar
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit

class RestApiClient {

// MARK: - Construction

    private constructor(builder: Builder) {
        _httpClientConfig = builder.httpClientConfig ?: DefaultHttpClientConfig.SHARED
    }

// MARK: - Methods

    fun get(requestEntity: RequestEntity<HttpBody>): HttpResult {
        return execute(HttpMethod.GET, requestEntity)
    }

    fun post(requestEntity: RequestEntity<HttpBody>): HttpResult {
        return execute(HttpMethod.POST, requestEntity)
    }

    fun put(requestEntity: RequestEntity<HttpBody>): HttpResult {
        return execute(HttpMethod.PUT, requestEntity)
    }

    fun patch(requestEntity: RequestEntity<HttpBody>): HttpResult {
        return execute(HttpMethod.PATCH, requestEntity)
    }

    fun delete(requestEntity: RequestEntity<HttpBody>): HttpResult {
        return execute(HttpMethod.DELETE, requestEntity)
    }

    fun head(requestEntity: RequestEntity<HttpBody>): HttpResult {
        return execute(HttpMethod.HEAD, requestEntity)
    }

    fun options(requestEntity: RequestEntity<HttpBody>): HttpResult {
        return execute(HttpMethod.OPTIONS, requestEntity)
    }

// MARK: - Private Methods

    private fun execute(httpMethod: HttpMethod, requestEntity: RequestEntity<HttpBody>): HttpResult {
        val request = createRequest(httpMethod, requestEntity)

        // Execute HTTP request
        return execute(request, requestEntity.cookieStore)
    }

    private fun execute(request: Request, cookieStore: CookieStore): HttpResult {
        var httpResult: HttpResult

        try {
            httpResult = try {
                // Create and execute HTTP request
                val response = createClient(cookieStore).newCall(request).execute()
                HttpResult.success(createResponseEntity(response, cookieStore))
            }
            catch (ex: HttpResponseException) {
                Logger.w(TAG, ex)

                // Handle interrupted HTTP requests
                HttpResult.success(createResponseEntity(ex.response, cookieStore))
            }
        }
        catch (ex: Exception) {
            Logger.w(TAG, ex)

            // Handle any other errors
            httpResult = HttpResult.failure(ex)
        }

        // Done
        return httpResult
    }

    private fun createRequest(httpMethod: HttpMethod, requestEntity: RequestEntity<HttpBody>): Request {
        val builder = Request.Builder()

        // Create request body
        var httpBody = requestEntity.body
        var requestBody: RequestBody? = null

        // NOTE: Workaround for OkHttp crash on POST with empty request body
        if (httpBody == null && httpMethod == HttpMethod.POST) {
            httpBody = EMPTY_HTTP_BODY
        }

        if (httpBody != null) {
            val mediaType = okhttp3.MediaType.parse(httpBody.mediaType.toString())
            requestBody = RequestBody.create(mediaType, httpBody.body)
        }

        // Build HTTP request
        val httpHeaders = requestEntity.httpHeaders

        builder.method(httpMethod.value, requestBody)
            .url(requestEntity.link.toString())
            .headers(mapping(httpHeaders))

        requestBody?.contentType()?.let { contentType ->
            builder.header(HttpHeaders.CONTENT_TYPE, contentType.toString())
        }

        return builder.build()
    }

    private fun createClient(cookieStore: CookieStore): OkHttpClient {

        val cookieManager = CookieManager(cookieStore, CookiePolicy.ACCEPT_ALL)
        val cookieJar: CookieJar = CompatJavaNetCookieJar(cookieManager)

        // Retrofit 2.0: The biggest update yet on the best HTTP Client Library for Android
        // @link http://inthecheesefactory.com/blog/retrofit-2.0/en
        // +> Certificate Pinning
        // +> RxJava Integration with CallAdapter

        val requestTimeoutConfig = _httpClientConfig.requestTimeoutConfig
            ?: DefaultRequestTimeoutConfig.SHARED

        val builder = SHARED_HTTP_CLIENT.newBuilder()
            // Set the timeout until a connection is established
            .connectTimeout(requestTimeoutConfig.connectionTimeout, TimeUnit.MILLISECONDS)
            // Set the default socket timeout which is the timeout for waiting for data
            .readTimeout(requestTimeoutConfig.readTimeout, TimeUnit.MILLISECONDS)
            // Set the handler that can accept cookies from incoming HTTP responses and provides cookies to outgoing HTTP requests
            .cookieJar(cookieJar)

        // Set a application interceptors
        _httpClientConfig.interceptors
            ?.forEach(builder::addInterceptor)

        // Set a network interceptors
        _httpClientConfig.networkInterceptors
            ?.forEach(builder::addNetworkInterceptor)

        // Configure secure HTTPS connections
        _httpClientConfig.tlsConfig?.let { tlsConfig ->

            tlsConfig.certificatePinner
                ?.let(builder::certificatePinner)

            tlsConfig.hostnameVerifier
                ?.let(builder::hostnameVerifier)

            tlsConfig.sslSocketFactory?.let { sslSocketFactory ->
                val trustManager = tlsConfig.trustManager

                when (trustManager) {
                    null -> builder.sslSocketFactory(sslSocketFactory)
                    else -> builder.sslSocketFactory(sslSocketFactory, trustManager)
                }
            }
        }

        // Done
        return builder.build()
    }

    @Throws(IOException::class)
    private fun createResponseEntity(response: Response, cookieStore: CookieStore): ResponseEntity<ByteArray> {

        val link = response.request().url().uri()
        val httpHeaders = mapping(response.headers())
        val httpStatus = HttpStatus.valueOf(response.code())

        // Handle HTTP response
        val builder = BasicResponseEntity.Builder<ByteArray>()
            .link(link)
            .httpHeaders(httpHeaders)
            .httpStatus(httpStatus)
            // @see https://tools.ietf.org/html/rfc7231#section-3.1.1.5
            .mediaType(MediaType.APPLICATION_OCTET_STREAM)
            .cookieStore(cookieStore)

        response.body()?.let { responseBody ->

            // Set MediaType
            responseBody.contentType()?.let { contentType ->
                val mediaType = MediaType.valueOf(contentType.toString())
                builder.mediaType(mediaType)
            }

            // Set response body
            val body = ArrayUtils.emptyToNull(responseBody.bytes())
            builder.body(body)
        }

        // Done
        return builder.build()
    }

    @Deprecated("Must be removed")
    fun mapping(headers: Headers?): HttpHeaders {
        val httpHeaders = HttpHeaders()

        // Map okhttp3.Headers to HttpHeaders
        headers?.names()?.forEach { name ->
            httpHeaders[name] = headers.values(name)
        }

        // Done
        return httpHeaders
    }

    @Deprecated("Must be removed")
    fun mapping(headers: HttpHeaders?): Headers {
        val builder = Headers.Builder()

        // Map HttpHeaders to okhttp3.Headers
        headers?.entries?.forEach { entry ->
            entry.value.forEach { value ->
                builder.add(entry.key, value)
            }
        }

        // Done
        return builder.build()
    }

// MARK: - Inner Types

    internal class Builder {

        internal var httpClientConfig: HttpClientConfig? = null
            private set

        internal fun httpClientConfig(httpClientConfig: HttpClientConfig): Builder {
            this.httpClientConfig = httpClientConfig.clone()
            return this
        }

        internal fun build(): RestApiClient {
            return RestApiClient(this)
        }
    }

    internal class HttpResponseException(val response: Response): IOException()

// MARK: - Companion

    companion object {
        private val TAG = RestApiClient::class.java.simpleName
        private val SHARED_HTTP_CLIENT = OkHttpClient.Builder().build()
        private val EMPTY_HTTP_BODY: HttpBody = ByteArrayBody()
    }

// MARK: - Variables

    private val _httpClientConfig: HttpClientConfig
}
