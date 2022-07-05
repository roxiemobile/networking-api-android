package com.roxiemobile.networkingapi.network.rest

import com.roxiemobile.networkingapi.network.rest.request.RequestEntity

interface Call<T> {

// MARK: - Properties

    /**
     * TODO
     */
    val tag: String?

    /**
     * The original request entity.
     */
    val requestEntity: RequestEntity<T>
}
