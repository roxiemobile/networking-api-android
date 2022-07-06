@file:Suppress("JoinDeclarationAndAssignment", "NOTHING_TO_INLINE", "unused")

package com.roxiemobile.networkingapi.network.http.body

import com.roxiemobile.androidcommons.logging.Logger
import com.roxiemobile.networkingapi.network.http.MediaType
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

class FormBody: HttpBody {

// MARK: - Construction

    private constructor(body: ByteArray) {
        this.mediaType = MediaType.APPLICATION_FORM_URLENCODED
        this.body = body
    }

// MARK: - Properties

    override val mediaType: MediaType

    override val body: ByteArray

// MARK: - Inner Types

    class Builder {

        fun put(name: String, value: String): Builder {
            name.trim().takeIf { it.isNotEmpty() }?.let {
                _syncLock.write {
                    _values[it] = value.trim()
                }
            }
            return this
        }

        fun build(): FormBody {
            return FormBody(createByteArray())
        }

        private fun createByteArray(): ByteArray {

            val encodedString = _syncLock.read {
                _values
                    .filter { it.key.isNotEmpty() }
                    .mapNotNull { encodeEntry(it) }
                    .joinToString("&")
            }

            return encodedString
                .toByteArray(Charsets.UTF_8)
        }

        private fun encodeEntry(entry: Map.Entry<String, String>): String? {
            return try {
                urlEncode(entry.key) + '=' + urlEncode(entry.value)
            }
            catch (ex: UnsupportedEncodingException) {
                Logger.w(TAG, ex)
                null
            }
        }

        @Throws(UnsupportedEncodingException::class)
        private inline fun urlEncode(value: String): String {
            return URLEncoder.encode(value, Charsets.UTF_8.name())
        }

        companion object {
            private val TAG = Builder::class.java.simpleName
        }

        private val _values = mutableMapOf<String, String>()
        private val _syncLock = ReentrantReadWriteLock()
    }
}
