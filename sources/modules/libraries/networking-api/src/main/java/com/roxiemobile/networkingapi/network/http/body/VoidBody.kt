@file:Suppress("unused")

package com.roxiemobile.networkingapi.network.http.body

import com.roxiemobile.networkingapi.network.http.MediaType

class VoidBody: HttpBody {

// MARK: - Construction

    private constructor() {
        throw UnsupportedOperationException()
    }

// MARK: - Methods

    override val mediaType: MediaType
        get() = throw UnsupportedOperationException()

    override val body: ByteArray
        get() = throw UnsupportedOperationException()
}
