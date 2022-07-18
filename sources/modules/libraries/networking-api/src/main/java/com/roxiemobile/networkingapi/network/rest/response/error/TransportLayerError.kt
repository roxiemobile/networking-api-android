package com.roxiemobile.networkingapi.network.rest.response.error

class TransportLayerError(cause: Throwable):
    AbstractRestApiError(cause)
