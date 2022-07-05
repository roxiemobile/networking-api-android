package com.roxiemobile.networkingapi.network.rest.response.error.nested

import com.annimon.stream.function.Consumer
import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity

interface ResponseEntityHolder {

// MARK: - Properties

    /**
     * Returns the HTTP response entity.
     */
    val responseEntity: ResponseEntity<ByteArray>

// MARK: - Methods

    /**
     * Returns the response body as a byte array.
     */
    fun responseBodyAsBytes(): ByteArray?

    /**
     * Returns the response body as a string.
     */
    fun responseBodyAsString(): String?

    /**
     * Sends a printable representation of this `RestApiError`'s description
     * to the consumer.
     */
    fun printErrorDescription(consumer: Consumer<String>)
}
