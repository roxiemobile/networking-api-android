package com.roxiemobile.networkingapi.network.rest

import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity
import com.roxiemobile.networkingapi.network.rest.response.RestApiError

interface Callback<Ti, To> {

// MARK: - Methods

    /**
     * TODO
     */
    fun onShouldExecute(call: Call<Ti>): Boolean {
        return true
    }

    /**
     * TODO
     */
    fun onSucceeded(call: Call<Ti>, responseEntity: ResponseEntity<To>) {
        // Do nothing
    }

    /**
     * TODO
     */
    fun onFailed(call: Call<Ti>, restApiError: RestApiError) {
        // Do nothing
    }

    /**
     * TODO
     */
    fun onCancelled(call: Call<Ti>) {
        // Do nothing
    }
}
