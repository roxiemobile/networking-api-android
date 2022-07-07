@file:Suppress("NOTHING_TO_INLINE", "unused")

package com.roxiemobile.networkingapi.network.rest.routing

import com.roxiemobile.androidcommons.logging.Logger
import java.io.UnsupportedEncodingException
import java.net.URI
import java.net.URISyntaxException
import java.net.URLEncoder

object HttpRouteFactory {

// MARK: - Methods

    @Throws(IllegalStateException::class)
    fun createRoute(
        baseLink: URI? = null,
        path: String? = null,
        parameters: QueryParameters? = null,
    ): HttpRoute {

        var linkText: String? = null

        // Build new link
        if (baseLink != null) {
            linkText = baseLink.toString()

            // Append path to link
            if (path != null) {
                linkText += path.trim()
            }

            // Append query parameters to link
            if (parameters != null && parameters.size > 0) {
                linkText += "?" + buildQueryString(parameters)
            }
        }

        // Build new HTTP route
        var httpRoute: HttpRoute? = null
        try {
            if (linkText != null) {
                httpRoute = HttpRoute(URI(linkText).normalize())
            }
        }
        catch (ex: URISyntaxException) {
            Logger.w(TAG, ex)
        }

        // Validate result
        if (httpRoute == null) {
            error("Failed to create HTTP route for path: ${path}")
        }

        // Done
        return httpRoute
    }

// MARK: - Private Methods

    @Throws(IllegalStateException::class)
    private fun buildQueryString(parameters: QueryParameters): String {
        val components = mutableListOf<String>()

        try {
            // Build query string components
            for (key in parameters.keys) {
                parameters[key]?.let { value ->
                    components.addAll(buildQueryStringComponents(key, value))
                }
            }
        }
        catch (ex: UnsupportedEncodingException) {
            throw IllegalStateException("Could not build query string.", ex)
        }

        // Done
        return components.joinToString("&")
    }

    @Throws(UnsupportedEncodingException::class)
    private fun buildQueryStringComponents(key: String, values: List<String>): List<String> {
        val components = mutableListOf<String>()

        if (values.isEmpty()) {
            // Do nothing
        }
        else if (values.size > 1) {
            for (value in values) {
                val encodedValue = urlEncode(key) + "[]=" + urlEncode(value)
                components.add(encodedValue)
            }
        }
        else {
            val encodedValue = urlEncode(key) + "=" + urlEncode(values[0])
            components.add(encodedValue)
        }

        // Done
        return components
    }

    @Throws(UnsupportedEncodingException::class)
    private inline fun urlEncode(value: String): String {
        return URLEncoder.encode(value, Charsets.UTF_8.name())
    }

// MARK: - Constants

    private val TAG = HttpRoute::class.java.simpleName
}
