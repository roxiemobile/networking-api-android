package com.roxiemobile.networkingapi.network.rest

import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity
import com.roxiemobile.networkingapi.network.rest.response.RestApiError

open class CallbackDecorator<Ti, To>(callback: Callback<Ti, To>?):
    Callback<Ti, To> {

// MARK: - Methods

    override fun onShouldExecute(call: Call<Ti>): Boolean {
        return _callback?.onShouldExecute(call) ?: true
    }

    override fun onSucceeded(call: Call<Ti>, responseEntity: ResponseEntity<To>) {
        _callback?.onSucceeded(call, responseEntity)
    }

    override fun onFailed(call: Call<Ti>, restApiError: RestApiError) {
        _callback?.onFailed(call, restApiError)
    }

    override fun onCancelled(call: Call<Ti>) {
        _callback?.onCancelled(call)
    }

// MARK: - Variables

    private val _callback: Callback<Ti, To>? = callback
}
