package com.roxiemobile.networkingapi.network.rest.response.error

import com.annimon.stream.function.Consumer
import com.roxiemobile.networkingapi.network.rest.response.error.nested.ResponseEntityHolder

class ApplicationLayerError(cause: Throwable):
    AbstractRestApiError(cause) {

// MARK: - Methods

    /**
     * Sends a printable representation of this `RestApiError`'s description
     * to the consumer.
     */
    override fun printErrorDescription(consumer: Consumer<String>) {
        val error = this.cause

        // Log nested error
        if (error is ResponseEntityHolder) {

            val responseEntityHolder = error as ResponseEntityHolder
            val responseEntity = responseEntityHolder.responseEntity

            // Send error description to consumer
            consumer.accept("Request link: " + responseEntity.link)
            consumer.accept("Status: " + responseEntity.httpStatus.value())

            // Good, but we need to go deeper :)
            responseEntityHolder.printErrorDescription(consumer)
        }
        else {
            super.printErrorDescription(consumer)
        }
    }
}
