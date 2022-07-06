@file:Suppress("KDocUnresolvedReference", "unused")

package com.roxiemobile.networkingapi.network.http

/**
 * Represents HTTP Bearer Authentication.
 *
 * @see [RFC6750](http://www.ietf.org/rfc/rfc6750.txt)
 */
class HttpBearerAuthentication(
    private val token: String,
): HttpAuthentication() {

// MARK: - Methods

    /**
     * Returns the value for the 'Authorization' HTTP header.
     */
    override fun getHeaderValue(): String {
        return "Bearer " + this.token
    }
}
