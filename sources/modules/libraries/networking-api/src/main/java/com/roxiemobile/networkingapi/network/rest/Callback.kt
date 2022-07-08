package com.roxiemobile.networkingapi.network.rest

import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity
import com.roxiemobile.networkingapi.network.rest.response.RestApiError

interface Callback<Ti, To> {

// MARK: - Methods

    /**
     * TODO
     */
    fun onShouldExecute(call: Call<Ti>): Boolean

    /**
     * TODO
     */
    fun onSuccess(call: Call<Ti>, responseEntity: ResponseEntity<To>)

    /**
     * TODO
     */
    fun onFailure(call: Call<Ti>, restApiError: RestApiError)

    /**
     * TODO
     */
    fun onCancel(call: Call<Ti>)
}
