package com.roxiemobile.networkingapi.network.rest.response.error.nested

import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity

class ConversionException(
    responseEntity: ResponseEntity<ByteArray>,
    cause: Throwable? = null,
): AbstractNestedException(responseEntity, cause)
