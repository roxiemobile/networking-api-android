@file:Suppress("CanBePrimaryConstructorProperty")

package com.roxiemobile.networkingapi.network.rest.response.error

import com.annimon.stream.function.Consumer
import com.roxiemobile.networkingapi.network.rest.response.RestApiError
import java.io.PrintWriter
import java.io.StringWriter

abstract class AbstractRestApiError(cause: Throwable? = null):
    RestApiError {

// MARK: - Properties

    /**
     * Returns the cause of this `RestApiError`, or `null` if there is no cause.
     */
    override val cause: Throwable? = cause

// MARK: - Methods

    /**
     * Sends a printable representation of this `RestApiError`'s description
     * to the consumer.
     */
    override fun printErrorDescription(consumer: Consumer<String>) {
        this.cause?.let { error ->

            StringWriter().use { stringWriter ->
                PrintWriter(stringWriter).use { printWriter ->

                    // Compose error description
                    printWriter.write(error.message + ": ")
                    error.printStackTrace(printWriter)
                    printWriter.flush()

                    // Send error description to consumer
                    consumer.accept(stringWriter.toString())
                }
            }
        }
    }
}
