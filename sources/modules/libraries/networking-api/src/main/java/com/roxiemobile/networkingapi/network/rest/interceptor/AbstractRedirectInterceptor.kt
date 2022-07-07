package com.roxiemobile.networkingapi.network.rest.interceptor

import com.roxiemobile.networkingapi.network.http.HttpHeaders
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.internal.http.RealResponseBody
import okio.GzipSource
import okio.Okio
import java.io.IOException

abstract class AbstractRedirectInterceptor: Interceptor {

// MARK: - Methods

    @Throws(IOException::class)
    override fun intercept(chain: Chain): Response {
        val request = chain.request()

        // Execute HTTP request
        var response = chain.proceed(request)
        if (response.isRedirect) {

            // Handle HTTP redirects
            response = onRedirect(response)
        }

        return response
    }

    /**
     * TODO
     */
    @Throws(IOException::class)
    abstract fun onRedirect(response: Response): Response

    /**
     * Returns a new response that does gzip decompression on `response`.
     */
    @Throws(IOException::class)
    protected fun decompressResponse(response: Response): Response {
        var newResponse = response

        val responseBody = response.body()
        if (responseBody != null && "gzip".equals(response.header(HttpHeaders.CONTENT_ENCODING), ignoreCase = true)) {

            // Uncompress a response body
            GzipSource(responseBody.source()).use { gzipSource ->

                val strippedHeaders = response.headers().newBuilder()
                    .removeAll(HttpHeaders.CONTENT_ENCODING)
                    .removeAll(HttpHeaders.CONTENT_LENGTH)
                    .build()

                // Create new HTTP response
                val newResponseBody = RealResponseBody(
                    strippedHeaders[HttpHeaders.CONTENT_TYPE],
                    strippedHeaders[HttpHeaders.CONTENT_LENGTH]?.toLongOrNull() ?: -1L,
                    Okio.buffer(gzipSource),
                )

                newResponse = response.newBuilder()
                    .headers(strippedHeaders)
                    .body(ResponseBody.create(newResponseBody.contentType(), newResponseBody.bytes()))
                    .build()
            }
        }

        return newResponse
    }
}
