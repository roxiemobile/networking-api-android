package com.roxiemobile.networkingapi.network.rest.response.error.nested

import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity

class UnexpectedMediaTypeException: AbstractNestedException {

// MARK: - Construction

    /**
     * Construct a new instance of `UnexpectedMediaTypeException` based on a [ResponseEntity] and cause.
     */
    @JvmOverloads
    constructor(responseEntity: ResponseEntity<ByteArray>, cause: Throwable? = null): super(responseEntity, cause)
}
