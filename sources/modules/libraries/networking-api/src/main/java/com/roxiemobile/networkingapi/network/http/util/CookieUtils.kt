@file:Suppress("unused")

package com.roxiemobile.networkingapi.network.http.util

import com.roxiemobile.androidcommons.logging.Logger
import com.roxiemobile.networkingapi.network.http.HttpCookie
import java.net.URI
import java.net.URISyntaxException

object CookieUtils {

// MARK: - Methods

    fun cookieLink(cookie: HttpCookie): URI? {
        var link: URI? = null

        try {
            link = URI(SCHEME, cookie.domain, null, null)
        }
        catch (ex: URISyntaxException) {
            Logger.w(TAG, ex)
        }

        return link
    }

    fun cookieLink(link: URI?): URI? {
        var newLink: URI? = null

        if (link != null) {
            try {
                newLink = URI(SCHEME, link.host, null, null)
            }
            catch (ex: URISyntaxException) {
                Logger.w(TAG, ex)
            }
        }

        return newLink
    }

    fun isNullOrExpired(cookie: HttpCookie?, offsetInMillis: Long = 0): Boolean {
        var expired = true

        cookie?.expiryDate?.let { expiryDate ->
            expired = (System.currentTimeMillis() + offsetInMillis) >= expiryDate.time
        }
        return expired
    }

// MARK: - Constants

    private val TAG = CookieUtils::class.java.simpleName

    private const val SCHEME = "https"
}
