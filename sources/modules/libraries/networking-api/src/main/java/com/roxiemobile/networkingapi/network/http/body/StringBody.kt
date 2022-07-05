@file:Suppress("JoinDeclarationAndAssignment", "unused")

package com.roxiemobile.networkingapi.network.http.body

import com.roxiemobile.networkingapi.network.http.MediaType

class StringBody: HttpBody {

// MARK: - Construction

    @JvmOverloads
    constructor(body: String = "") {
        this.mediaType = MEDIA_TYPE
        this.body = body.toByteArray(Charsets.UTF_8)
    }

// MARK: - Properties

    override val mediaType: MediaType

    override val body: ByteArray

// MARK: - Companion

    companion object {

        private val MEDIA_TYPE = MediaType
            .valueOf(MediaType.TEXT_PLAIN_VALUE + "; charset=" + Charsets.UTF_8.name())
    }
}
