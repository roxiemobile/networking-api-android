package com.roxiemobile.networkingapi.network.rest

import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity
import com.roxiemobile.networkingapi.network.rest.response.RestApiError

@Deprecated("Must be removed", ReplaceWith("kotlin.Result"))
class CallResult<T>: Result<ResponseEntity<T>, RestApiError, T> {

// MARK: - Construction

    private constructor(responseEntity: ResponseEntity<T>): super(responseEntity)

    private constructor(error: RestApiError): super(error)

// MARK: - Companion

    companion object {

        fun <T> success(responseEntity: ResponseEntity<T>): CallResult<T> {
            return CallResult(responseEntity)
        }

        fun <T> failure(error: RestApiError): CallResult<T> {
            return CallResult(error)
        }
    }
}
