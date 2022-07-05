package com.roxiemobile.networkingapi.network.rest.response.error.nested

class ConnectionException: Exception {

// MARK: - Construction

    /**
     * Construct a new instance of `ConnectionException`.
     */
    @JvmOverloads
    constructor(cause: Throwable? = null): super(cause)
}
