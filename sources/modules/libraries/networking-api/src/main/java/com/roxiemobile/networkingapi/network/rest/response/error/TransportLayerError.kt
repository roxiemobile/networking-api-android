package com.roxiemobile.networkingapi.network.rest.response.error

import com.roxiemobile.networkingapi.network.rest.response.RestApiError.RestApiErrorType

class TransportLayerError(cause: Throwable): AbstractRestApiError(cause) {

// MARK: - Properties

    /**
     * Returns the type of an error.
     */
    override val type = RestApiErrorType.TRANSPORT_LAYER
}
