package com.roxiemobile.networkingapi.network.rest.routing

import java.net.URI

@JvmInline
value class HttpRoute(val link: URI) {

// MARK: - Methods

    override fun toString(): String {
        return this.link.toString()
    }
}
