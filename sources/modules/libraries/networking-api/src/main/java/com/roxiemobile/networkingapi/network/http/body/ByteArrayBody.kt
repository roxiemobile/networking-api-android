@file:Suppress("JoinDeclarationAndAssignment")

package com.roxiemobile.networkingapi.network.http.body

import com.roxiemobile.networkingapi.network.http.MediaType

class ByteArrayBody: HttpBody {

// MARK: - Construction

    @JvmOverloads
    constructor(body: ByteArray = byteArrayOf()) {
        this.mediaType = MediaType.APPLICATION_OCTET_STREAM
        this.body = body
    }

// MARK: - Properties

    override val mediaType: MediaType

    override val body: ByteArray
}
