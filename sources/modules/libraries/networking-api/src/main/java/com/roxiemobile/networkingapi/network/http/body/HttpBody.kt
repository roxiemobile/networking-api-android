package com.roxiemobile.networkingapi.network.http.body

import com.roxiemobile.networkingapi.network.http.MediaType

interface HttpBody {

// MARK: - Properties

    /**
     * TODO
     */
    val mediaType: MediaType

    /**
     * TODO
     */
    val body: ByteArray
}
