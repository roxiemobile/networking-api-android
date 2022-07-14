@file:Suppress("UsePropertyAccessSyntax", "unused")

package com.roxiemobile.networkingapi.network.http

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.roxiemobile.androidcommons.logging.Logger
import com.roxiemobile.androidcommons.util.IOUtils
import com.roxiemobile.kotlincommons.util.Base62
import com.roxiemobile.networkingapi.network.http.util.CookieUtils
import com.roxiemobile.networkingapi.network.http.util.LinkedMultiValueMap
import com.roxiemobile.networkingapi.network.http.util.MultiValueMap
import kotlinx.collections.immutable.toImmutableList
import java.io.StreamCorruptedException
import java.net.URI
import java.util.Date

class PersistentCookieStore: CookieStore {

// MARK: - Construction

    constructor(context: Context) {
        _sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        loadFromSharedPreferences()
    }

    constructor(context: Context, cookies: List<HttpCookie>?): this(context) {
        if (cookies != null) {
            for (cookie in cookies) {
                add(CookieUtils.cookieLink(cookie), cookie)
            }
        }
    }

    constructor(context: Context, otherCookieStore: CookieStore?): this(context) {
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

        saveToSharedPreferences(link, cookie)
        _cookies.add(link, cookie)
    }

    @Synchronized
    override fun get(uri: URI?): List<HttpCookie> {
        val link = CookieUtils.cookieLink(uri)

        val outCookies = mutableListOf<HttpCookie>()
        val expiredCookies = mutableListOf<HttpCookie>()
        val date = Date()

        // Get cookies associated with given link. If none, returns an empty list
        _cookies[link]?.let { cookies ->
            val iterator = cookies.iterator()

            while (iterator.hasNext()) {
                val cookie = iterator.next()

                if (cookie.hasExpired(date)) {
                    iterator.remove() // remove expired cookies
                    expiredCookies.add(cookie)
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
                            expiredCookies.add(cookie)
                        }
                        else if (!outCookies.contains(cookie)) {
                            outCookies.add(cookie)
                        }
                    }
                }
            }
        }

        if (expiredCookies.isNotEmpty()) {
            removeFromSharedPreferences(link, expiredCookies)
            expiredCookies.clear()
        }

        return outCookies.toImmutableList()
    }

    @Synchronized
    override fun getCookies(): List<HttpCookie> {

        val outCookies = mutableListOf<HttpCookie>()
        val expiredCookies = mutableListOf<HttpCookie>()
        val date = Date()

        for (link in _cookies.keys) {

            _cookies[link]?.let { cookies ->
                val iterator = cookies.iterator()

                while (iterator.hasNext()) {
                    val cookie = iterator.next()

                    if (cookie.hasExpired(date)) {
                        iterator.remove() // remove expired cookies
                        expiredCookies.add(cookie)
                    }
                    else if (!outCookies.contains(cookie)) {
                        outCookies.add(cookie)
                    }
                }
            }

            if (expiredCookies.isNotEmpty()) {
                removeFromSharedPreferences(link, expiredCookies)
                expiredCookies.clear()
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
        var result = false

        _cookies[link]?.let { cookies ->
            result = cookies.remove(cookie)

            if (cookies.isEmpty()) {
                // Remove key without values
                _cookies.remove(link)
            }

            if (result) {
                removeFromSharedPreferences(link, cookie)
            }
        }

        return result
    }

    @Synchronized
    override fun removeAll(): Boolean {
        return _cookies.isNotEmpty().also {
            clearSharedPreferences()
            _cookies.clear()
        }
    }

// MARK: - Private Methods

    private fun loadFromSharedPreferences() {
        _cookies.clear()

        for (entry in _sharedPreferences.all) {
            try {

                val storageKey = decodeKey(entry.key)
                val decodedObject = IOUtils.decodeObject(entry.value as String)

                (decodedObject as? HttpCookie)?.let { cookie ->
                    _cookies.add(storageKey.link, cookie)
                }
            }
            catch (ex: StreamCorruptedException) {
                Logger.w(TAG, ex)
            }
        }
    }

    private fun saveToSharedPreferences(link: URI?, cookie: HttpCookie) {
        _sharedPreferences.edit {
            val storageKey = encodeKey(StorageKey(link, cookie.name))
            val value = IOUtils.encodeObject(cookie)
            putString(storageKey, value)
        }
    }

    private fun removeFromSharedPreferences(link: URI?, cookies: List<HttpCookie>) {
        _sharedPreferences.edit {
            cookies.forEach { cookie ->
                val storageKey = encodeKey(StorageKey(link, cookie.name))
                remove(storageKey)
            }
        }
    }

    private fun removeFromSharedPreferences(link: URI?, cookie: HttpCookie) {
        _sharedPreferences.edit {
            val storageKey = encodeKey(StorageKey(link, cookie.name))
            remove(storageKey)
        }
    }

    private fun clearSharedPreferences() {
        _sharedPreferences.edit {
            clear()
        }
    }

    private fun encodeKey(storageKey: StorageKey): String {

        val components = listOf(
            storageKey.link.toString(),
            storageKey.cookieName,
        )

        return components.joinToString(KEY_DELIMITER) {
            Base62.SHARED.encode(it.toByteArray()).toString()
        }
    }

    @Throws(StreamCorruptedException::class)
    private fun decodeKey(key: String): StorageKey {
        try {
            val components = key.split(KEY_DELIMITER, limit = 2).map {
                Base62.SHARED.decode(it.toByteArray()).toString()
            }

            val linkText = components[0]
            val link = URI.create(if (linkText != "null") linkText else null)

            return StorageKey(link, components[1])
        }
        catch (ex: Exception) {
            throw StreamCorruptedException()
        }
    }

// MARK: - Inner Types

    private data class StorageKey(val link: URI?, val cookieName: String)

// MARK: - Companion

    companion object {

        private val TAG = PersistentCookieStore::class.java.simpleName

        private const val SHARED_PREFS_NAME = "com.roxiemobile.networkingapi.network.http.cookieStore"
        private const val KEY_DELIMITER = ":"
    }

// MARK: - Variables

    private val _sharedPreferences: SharedPreferences

    // NOTE: This map may have null keys!
    private val _cookies: MultiValueMap<URI?, HttpCookie> = LinkedMultiValueMap()
}
