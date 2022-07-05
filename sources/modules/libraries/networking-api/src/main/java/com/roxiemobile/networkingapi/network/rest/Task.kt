package com.roxiemobile.networkingapi.network.rest

interface Task<Ti, To>: Call<Ti>, Cloneable {

// MARK: - Methods

    /**
     * TODO
     */
    fun execute(callback: Callback<Ti, To>?)

    /**
     * TODO
     */
    fun enqueue(callback: Callback<Ti, To>?, callbackOnUiThread: Boolean): Cancellable

    /**
     * TODO
     */
    public override fun clone(): Task<Ti, To>
}
