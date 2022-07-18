package com.roxiemobile.networkingapi.network.rest.response.error.nested

import com.annimon.stream.function.Consumer
import com.roxiemobile.androidcommons.logging.Logger
import com.roxiemobile.networkingapi.network.rest.converter.StringConverter
import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity

abstract class AbstractNestedException(
    responseEntity: ResponseEntity<ByteArray>,
    cause: Throwable? = null,
): Exception(cause),
    ResponseEntityHolder {

// MARK: - Properties

    /**
     * Returns the HTTP response entity.
     */
    override val responseEntity: ResponseEntity<ByteArray>
        get() = _responseEntity

// MARK: - Methods

    /**
     * Returns the response body as a byte array.
     */
    override fun responseBodyAsBytes(): ByteArray? {
        return _responseEntity.body
    }

    /**
     * Returns the response body as a string.
     */
    override fun responseBodyAsString(): String? {
        try {
            return CONVERTER.convert(_responseEntity).body
        }
        catch (ex: ConversionException) {
            throw InternalError(ex.message)
        }
    }

    /**
     * Sends a printable representation of this `RestApiError`'s description
     * to the consumer.
     */
    override fun printErrorDescription(consumer: Consumer<String>) {
        try {
            val message = responseBodyAsString()
            if (message?.isNotEmpty() == true) {
                consumer.accept(message)
            }
        }
        catch (ex: Exception) {
            Logger.w(TAG, ex)
        }
    }

// MARK: - Constants

    companion object {
        private val TAG = AbstractNestedException::class.java.simpleName
        private val CONVERTER = StringConverter()
    }

// MARK: - Variables

    private val _responseEntity: ResponseEntity<ByteArray> = responseEntity
}
