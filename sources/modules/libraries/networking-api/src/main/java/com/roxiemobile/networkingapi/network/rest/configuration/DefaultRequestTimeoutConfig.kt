package com.roxiemobile.networkingapi.network.rest.configuration

import java.util.concurrent.TimeUnit

data class DefaultRequestTimeoutConfig(
    override val connectionTimeout: Long = TimeUnit.SECONDS.toMillis(30),
    override val readTimeout: Long = TimeUnit.SECONDS.toMillis(15),
): RequestTimeoutConfig {

// MARK: - Construction

    init {
        require(this.connectionTimeout >= 0) { "connectionTimeout is less than 0" }
        require(this.readTimeout >= 0) { "readTimeout is less than 0" }
    }

// MARK: - Methods

    override fun clone(): RequestTimeoutConfig {
        return copy()
    }

// MARK: - Companion

    companion object {

        @JvmField
        val SHARED = DefaultRequestTimeoutConfig()
    }
}
