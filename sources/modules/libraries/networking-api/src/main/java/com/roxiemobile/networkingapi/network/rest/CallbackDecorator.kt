package com.roxiemobile.networkingapi.network.rest

import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity
import com.roxiemobile.networkingapi.network.rest.response.RestApiError

open class CallbackDecorator<Ti, To>: Callback<Ti, To> {

// MARK: - Construction

    @JvmOverloads
    constructor(callback: Callback<Ti, To>? = null) {
        _callback = callback
    }

// MARK: - Methods

    override fun onShouldExecute(call: Call<Ti>): Boolean {
        return _callback?.onShouldExecute(call) ?: true
    }

    override fun onSuccess(call: Call<Ti>, responseEntity: ResponseEntity<To>) {
        _callback?.onSuccess(call, responseEntity)
    }

    override fun onFailure(call: Call<Ti>, error: RestApiError) {
        _callback?.onFailure(call, error)
    }

    override fun onCancel(call: Call<Ti>) {
        _callback?.onCancel(call)
    }

// MARK: - Variables

    private val _callback: Callback<Ti, To>?
}
