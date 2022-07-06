@file:Suppress("KDocUnresolvedReference", "unused")

package com.roxiemobile.networkingapi.network.http

import android.util.Base64

/**
 * Represents HTTP Basic Authentication.
 *
 * @see [RFC2617](http://www.ietf.org/rfc/rfc2617.txt)
 */
class HttpBasicAuthentication(
    private val username: String,
    private val password: String,
): HttpAuthentication() {

// MARK: - Methods

    override fun getHeaderValue(): String {
        val bytes = (this.username + ":" + this.password).toByteArray()
        return "Basic " + Base64.encodeToString(bytes, Base64.NO_WRAP)
    }
}
