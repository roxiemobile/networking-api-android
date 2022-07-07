@file:Suppress("ConvertToStringTemplate")

package com.roxiemobile.networkingapi.network.rest.interceptor

import com.roxiemobile.networkingapi.network.http.HttpHeaders
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.Version

/**
 * This interceptor adds a custom User-Agent.
 */
class UserAgentRequestInterceptor: Interceptor {

// MARK: - Methods

    override fun intercept(chain: Chain): Response {
        val request: Request = chain.request()

        val newRequest = request.newBuilder()
            .header(HttpHeaders.USER_AGENT, createUserAgent(request.headers()))
            .build()

        return chain.proceed(newRequest)
    }

// MARK: - Private Methods

    private fun createUserAgent(headers: Headers): String {

        val userAgentText = headers.values(HttpHeaders.USER_AGENT)
            .filterNot { it.contains(OKHTTP_VERSION) }
            .map { it + " " + OKHTTP_VERSION }
            .firstOrNull()

        return userAgentText ?: OKHTTP_VERSION
    }

// MARK: - Constants

    companion object {
        val OKHTTP_VERSION: String = Version.userAgent()
    }
}
