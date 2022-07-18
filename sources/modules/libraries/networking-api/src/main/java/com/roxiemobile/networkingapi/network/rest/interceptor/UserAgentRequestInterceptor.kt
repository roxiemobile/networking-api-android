@file:Suppress("ConvertToStringTemplate")

package com.roxiemobile.networkingapi.network.rest.interceptor

import com.roxiemobile.networkingapi.network.http.HttpHeaders
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.Request
import okhttp3.Response

/**
 * This interceptor adds a custom User-Agent.
 */
class UserAgentRequestInterceptor: Interceptor {

// MARK: - Methods

    override fun intercept(chain: Chain): Response {
        val request: Request = chain.request()

        val newRequest = request.newBuilder()
            .header(HttpHeaders.USER_AGENT, createUserAgent(request.headers))
            .build()

        return chain.proceed(newRequest)
    }

// MARK: - Private Methods

    private fun createUserAgent(headers: Headers): String {

        val userAgentText = headers.values(HttpHeaders.USER_AGENT)
            .filterNot { it.contains(OKHTTP_USER_AGENT) }
            .map { it + " " + OKHTTP_USER_AGENT }
            .firstOrNull()

        return userAgentText ?: OKHTTP_USER_AGENT
    }

// MARK: - Constants

    companion object {
        const val OKHTTP_USER_AGENT: String = okhttp3.internal.userAgent
    }
}
