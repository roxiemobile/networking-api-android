package com.roxiemobile.networkingapi.network.rest.configuration

interface RequestTimeoutConfig: Cloneable {

// MARK: - Properties

    /**
     * Gets connection timeout.
     * @return the connection timeout in TimeUnit.MILLISECONDS
     */
    val connectionTimeout: Long

    /**
     * Gets read timeout.
     * @return the read timeout in TimeUnit.MILLISECONDS
     */
    val readTimeout: Long

// MARK: - Methods

    /**
     * TODO
     */
    public override fun clone(): RequestTimeoutConfig
}
