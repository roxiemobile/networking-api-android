package com.roxiemobile.networkingapi.network.rest

import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity

// Best way to return status flag and message from a method in Java
// @link http://stackoverflow.com/a/356293

@Deprecated("Must be removed", ReplaceWith("kotlin.Result"))
open class Result<S: ResponseEntity<BodyType>, F, BodyType> {

// MARK: - Construction

    internal constructor(success: S) {
        _state = State.SUCCESS
        _success = success
        _failure = null
    }

    internal constructor(failure: F) {
        _state = State.FAILURE
        _success = null
        _failure = failure
    }

// MARK: - Properties

    val isSuccess: Boolean
        get() = (_state == State.SUCCESS)

// MARK: - Methods

    fun value(): S? {
        return if (_state == State.SUCCESS) _success else null
    }

    fun error(): F? {
        return if (_state == State.FAILURE) _failure else null
    }

// MARK: - Inner Types

    private enum class State {
        SUCCESS, FAILURE
    }

// MARK: - Variables

    private val _state: State

    private val _success: S?
    private val _failure: F?
}
