@file:Suppress("unused")

package com.roxiemobile.networkingapi.network.rest.routing

import com.roxiemobile.networkingapi.network.http.util.LinkedMultiValueMap
import com.roxiemobile.networkingapi.network.http.util.MultiValueMap

class QueryParameters: LinkedMultiValueMap<String, String> {

// MARK: - Construction

    constructor(): super()

    constructor(initialCapacity: Int): super(initialCapacity)

    constructor(otherParameters: MultiValueMap<String, String>?): super(otherParameters ?: emptyMap())
}
