package com.roxiemobile.networkingapi.network.rest.converter

import com.roxiemobile.networkingapi.network.http.MediaType
import com.roxiemobile.networkingapi.network.rest.CallResult
import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity
import com.roxiemobile.networkingapi.network.rest.response.error.ApplicationLayerError
import com.roxiemobile.networkingapi.network.rest.response.error.nested.ConversionException
import com.roxiemobile.networkingapi.network.rest.response.error.nested.UnexpectedMediaTypeException

abstract class AbstractCallResultConverter<T>: CallResultConverter<ByteArray, T> {

// MARK: - Methods

    override fun convert(callResult: CallResult<ByteArray>): CallResult<T> {

        // Handle call result
        val newCallResult = callResult.fold(
            onSuccess = { responseEntity ->
                try {
                    checkMediaType(responseEntity)

                    // Convert response entity
                    val newResponseEntity = convert(responseEntity)
                    CallResult.success(newResponseEntity)
                }
                catch (ex: Exception) {
                    when (ex) {
                        is ConversionException,
                        is UnexpectedMediaTypeException -> {
                            // Build new error with caught exception
                            CallResult.failure(ApplicationLayerError(ex))
                        }
                        else -> {
                            throw ex
                        }
                    }
                }
            },
            onFailure = { exception ->
                CallResult.failure(exception)
            },
        )

        return newCallResult
    }

    protected abstract fun supportedMediaTypes(): Array<MediaType>

// MARK: - Private Methods

    @Throws(UnexpectedMediaTypeException::class)
    private fun checkMediaType(responseEntity: ResponseEntity<ByteArray>) {

        val mediaType = responseEntity.mediaType

        // Search for compatible MediaType
        val found = supportedMediaTypes()
            .any { mediaType.isCompatibleWith(it) }

        // Throw exception if on MediaType found
        if (!found) {
            throw UnexpectedMediaTypeException(responseEntity)
        }
    }
}
