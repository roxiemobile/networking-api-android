@file:Suppress("unused", "UsePropertyAccessSyntax")

package com.roxiemobile.networkingapi.network.http

import com.roxiemobile.networkingapi.network.http.util.CookieUtils
import com.roxiemobile.networkingapi.network.http.util.LinkedMultiValueMap
import com.roxiemobile.networkingapi.network.http.util.MultiValueMap
import kotlinx.collections.immutable.toImmutableList
import java.net.URI
import java.util.Date

class InMemoryCookieStore: CookieStore {

// MARK: - Construction

    constructor()

    constructor(cookies: List<HttpCookie>?) {
        if (cookies != null) {
            for (cookie in cookies) {
                add(CookieUtils.cookieLink(cookie), cookie)
            }
        }
    }

    constructor(otherCookieStore: CookieStore?) {
        if (otherCookieStore != null) {
            for (link in otherCookieStore.getURIs()) {
                for (cookie in otherCookieStore[link]) {
                    add(link, cookie)
                }
            }
        }
    }

// MARK: - Methods

    @Synchronized
    override fun add(uri: URI?, cookie: HttpCookie) {
        val link = CookieUtils.cookieLink(uri)

        val cookies: MutableList<HttpCookie>? = _cookies[link]
        cookies?.remove(cookie)

        _cookies.add(link, cookie)
    }

    @Synchronized
    override fun get(uri: URI?): List<HttpCookie> {
        val link = CookieUtils.cookieLink(uri)

        val outCookies = mutableListOf<HttpCookie>()
        val date = Date()

        // Get cookies associated with given link. If none, returns an empty list
        _cookies[link]?.let { cookies ->
            val iterator = cookies.iterator()

            while (iterator.hasNext()) {
                val cookie = iterator.next()

                if (cookie.hasExpired(date)) {
                    iterator.remove() // remove expired cookies
                }
                else {
                    outCookies.add(cookie)
                }
            }
        }

        // Get all cookies that domain matches the link
        if (link != null) {
            for (entry in _cookies.entries) {

                if (link == entry.key) {
                    continue // skip the given link; we've already handled it
                }

                val entryCookies: MutableList<HttpCookie> = entry.value
                val iterator = entryCookies.iterator()

                while (iterator.hasNext()) {
                    val cookie = iterator.next()

                    if (HttpCookie.domainMatches(cookie.domain, link.host)) {

                        if (cookie.hasExpired(date)) {
                            iterator.remove() // remove expired cookies
                        }
                        else if (!outCookies.contains(cookie)) {
                            outCookies.add(cookie)
                        }
                    }
                }
            }
        }

        return outCookies.toImmutableList()
    }

    @Synchronized
    override fun getCookies(): List<HttpCookie> {

        val outCookies = mutableListOf<HttpCookie>()
        val date = Date()

        for (cookies in _cookies.values) {
            val iterator = cookies.iterator()

            while (iterator.hasNext()) {
                val cookie = iterator.next()

                if (cookie.hasExpired(date)) {
                    iterator.remove() // remove expired cookies
                }
                else if (!outCookies.contains(cookie)) {
                    outCookies.add(cookie)
                }
            }
        }

        return outCookies.toImmutableList()
    }

    @Synchronized
    override fun getURIs(): List<URI> {
        val links = _cookies.keys.filterNotNull()
        return links.toImmutableList()
    }

    @Synchronized
    override fun remove(uri: URI?, cookie: HttpCookie): Boolean {
        val link = CookieUtils.cookieLink(uri)
        return (_cookies[link]?.remove(cookie) == true)
    }

    @Synchronized
    override fun removeAll(): Boolean {
        return _cookies.isNotEmpty().also {
            _cookies.clear()
        }
    }

// MARK: - Variables

    // NOTE: This map may have null keys!
    private val _cookies: MultiValueMap<URI?, HttpCookie> = LinkedMultiValueMap()
}
