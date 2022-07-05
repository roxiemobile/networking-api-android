package com.roxiemobile.networkingapi.network.rest.response

import com.annimon.stream.function.Consumer

interface RestApiError {

// MARK: - Properties

    /**
     * Returns the type of an error.
     */
    val type: RestApiErrorType

    /**
     * Returns the cause of this `RestApiError`, or `null` if there is no cause.
     */
    val cause: Throwable?

// MARK: - Methods

    /**
     * Sends a printable representation of this `RestApiError`'s description
     * to the consumer.
     */
    fun printErrorDescription(consumer: Consumer<String>)

// MARK: - Inner Types

    /**
     * The type of an error.
     */
    enum class RestApiErrorType {
        TRANSPORT_LAYER, APPLICATION_LAYER, TOP_LEVEL_PROTOCOL
    }
}
