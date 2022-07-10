package com.roxiemobile.networkingapi.network.rest.request

import com.roxiemobile.networkingapi.network.http.body.HttpBody
import com.roxiemobile.networkingapi.network.rest.Task
import com.roxiemobile.networkingapi.network.rest.configuration.HttpClientConfig

interface TaskBuilder<Ti: HttpBody, To>: Cloneable {

// MARK: - Methods

    /**
     * TODO
     */
    fun tag(tag: String?): TaskBuilder<Ti, To>

    /**
     * TODO
     */
    fun requestEntity(requestEntity: RequestEntity<Ti>): TaskBuilder<Ti, To>

    /**
     * TODO
     */
    fun httpClientConfig(httpClientConfig: HttpClientConfig): TaskBuilder<Ti, To>

    /**
     * TODO
     */
    fun build(): Task<Ti, To>

    /**
     * TODO
     */
    public override fun clone(): TaskBuilder<Ti, To>
}
