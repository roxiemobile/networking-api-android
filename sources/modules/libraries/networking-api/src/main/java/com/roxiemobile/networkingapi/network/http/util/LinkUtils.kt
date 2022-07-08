@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.roxiemobile.networkingapi.network.http.util

import java.net.URI
import java.net.URL

object LinkUtils {

// MARK: - Methods

    fun getEffectivePort(link: URI): Int {
        return getEffectivePort(link.scheme, link.port)
    }

    fun getEffectivePort(link: URL): Int {
        return getEffectivePort(link.protocol, link.port)
    }

    fun getDefaultPort(scheme: String): Int {
        return when (scheme.lowercase()) {
            "http" -> 80
            "https" -> 443
            else -> -1
        }
    }

// MARK: - Private Methods

    private fun getEffectivePort(scheme: String, specifiedPort: Int = -1): Int {
        return if (specifiedPort != -1) specifiedPort else getDefaultPort(scheme)
    }
}
