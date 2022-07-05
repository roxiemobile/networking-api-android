package com.roxiemobile.networkingapi.network.rest.request

import com.roxiemobile.networkingapi.network.rest.Task

interface TaskBuilder<Ti, To> {

// MARK: - Properties

    /**
     * TODO
     */
    val tag: String?

    /**
     * The original HTTP request.
     */
    val requestEntity: RequestEntity<Ti>?

// MARK: - Methods

    /**
     * TODO
     */
    fun build(): Task<Ti, To>
}
