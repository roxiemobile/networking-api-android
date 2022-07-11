@file:Suppress("RedundantVisibilityModifier")

package com.roxiemobile.networkingapi.network.rest.response

import com.roxiemobile.networkingapi.network.http.HttpStatus
import com.roxiemobile.networkingapi.network.http.MediaType
import com.roxiemobile.networkingapi.network.rest.request.RequestEntity

interface ResponseEntity<TBody>: RequestEntity<TBody> {

// MARK: - Properties

    /**
     * TODO
     */
    val httpStatus: HttpStatus

    /**
     * TODO
     */
    val mediaType: MediaType

// MARK: - Methods

    /**
     * TODO
     */
    public override fun clone(): ResponseEntity<TBody>
}
