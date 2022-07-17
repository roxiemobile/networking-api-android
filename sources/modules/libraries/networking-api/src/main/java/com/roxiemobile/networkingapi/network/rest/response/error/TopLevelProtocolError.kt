@file:Suppress("CanBePrimaryConstructorProperty", "unused")

package com.roxiemobile.networkingapi.network.rest.response.error

import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity

open class TopLevelProtocolError(responseEntity: ResponseEntity<*>):
    AbstractRestApiError() {

// MARK: - Properties

    /**
     * Returns the HTTP response entity.
     */
    val responseEntity: ResponseEntity<*> = responseEntity
}
