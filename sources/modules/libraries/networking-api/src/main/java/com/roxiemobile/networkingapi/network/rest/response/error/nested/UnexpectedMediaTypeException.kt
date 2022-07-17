package com.roxiemobile.networkingapi.network.rest.response.error.nested

import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity

class UnexpectedMediaTypeException(
    responseEntity: ResponseEntity<ByteArray>,
    cause: Throwable? = null,
): AbstractNestedException(responseEntity, cause)
