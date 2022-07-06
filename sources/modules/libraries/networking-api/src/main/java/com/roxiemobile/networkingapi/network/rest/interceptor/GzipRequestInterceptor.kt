package com.roxiemobile.networkingapi.network.rest.interceptor

import com.roxiemobile.networkingapi.network.http.ContentCodingType
import com.roxiemobile.networkingapi.network.http.HttpHeaders
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.Response
import okio.Buffer
import okio.BufferedSink
import okio.GzipSink
import okio.Okio
import java.io.IOException

/**
 * This interceptor compresses the HTTP request body.
 */
class GzipRequestInterceptor: Interceptor {

// MARK: - Methods

    @Throws(IOException::class)
    override fun intercept(chain: Chain): Response {
        val request = chain.request()

        if (request.body() == null || request.header(HttpHeaders.CONTENT_ENCODING) != null) {
            return chain.proceed(request)
        }

        val newRequest = request.newBuilder()
            .header(HttpHeaders.CONTENT_ENCODING, ContentCodingType.GZIP_VALUE)
            .method(request.method(), requestBodyWithContentLength(gzip(request.body())))
            .build()

        return chain.proceed(newRequest)
    }

// MARK: - Private Methods

    private fun gzip(requestBody: RequestBody?): RequestBody {
        return object: RequestBody() {

            override fun contentType(): MediaType? {
                return requestBody?.contentType()
            }

            override fun contentLength(): Long {
                // NOTE: We don't know the compressed length in advance!
                return -1L
            }

            override fun writeTo(sink: BufferedSink) {
                GzipSink(sink).use {
                    Okio.buffer(it).use { gzipSink ->
                        requestBody?.writeTo(gzipSink)
                    }
                }
            }
        }
    }

    // Add GZip Request Compression
    // @link https://github.com/square/okhttp/issues/350#issuecomment-123105641

    @Throws(IOException::class)
    private fun requestBodyWithContentLength(requestBody: RequestBody): RequestBody {

        val buffer = Buffer().also {
            requestBody.writeTo(it)
        }

        return object: RequestBody() {

            override fun contentType(): MediaType? {
                return requestBody.contentType()
            }

            override fun contentLength(): Long {
                return buffer.size()
            }

            override fun writeTo(sink: BufferedSink) {
                sink.write(buffer.snapshot())
            }
        }
    }
}
