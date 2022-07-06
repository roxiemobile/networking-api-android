@file:Suppress("CanBePrimaryConstructorProperty", "unused")

package com.roxiemobile.networkingapi.network.rest.response.error

import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity
import com.roxiemobile.networkingapi.network.rest.response.RestApiError.RestApiErrorType

abstract class TopLevelProtocolError<T>(responseEntity: ResponseEntity<T>):
    AbstractRestApiError() {

// MARK: - Properties

    /**
     * Returns the type of an error.
     */
    override val type = RestApiErrorType.TOP_LEVEL_PROTOCOL

    /**
     * Returns the response entity.
     */
    val responseEntity: ResponseEntity<T> = responseEntity
}
