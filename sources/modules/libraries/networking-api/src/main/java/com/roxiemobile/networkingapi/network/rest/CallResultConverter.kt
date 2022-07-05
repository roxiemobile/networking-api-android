package com.roxiemobile.networkingapi.network.rest

import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity
import com.roxiemobile.networkingapi.network.rest.response.error.nested.ConversionException

interface CallResultConverter<Ti, To> {

// MARK: - Methods

    /**
     * Converts result from one format to another.
     */
    fun convert(callResult: CallResult<Ti>): CallResult<To>

    /**
     * Converts response entity from one format to another.
     */
    @Throws(ConversionException::class)
    fun convert(responseEntity: ResponseEntity<Ti>): ResponseEntity<To>
}
