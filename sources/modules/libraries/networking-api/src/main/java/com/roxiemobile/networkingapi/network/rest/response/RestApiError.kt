package com.roxiemobile.networkingapi.network.rest.response

import com.annimon.stream.function.Consumer

abstract class RestApiError(cause: Throwable? = null):
    Error(cause) {

// MARK: - Methods

    /**
     * Sends a printable representation of this `RestApiError`'s description
     * to the consumer.
     */
    abstract fun printErrorDescription(consumer: Consumer<String>)
}
