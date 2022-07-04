package com.roxiemobile.networkingapi.network.rest;

// Best way to return status flag and message from a method in Java
// @link http://stackoverflow.com/a/356293

import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class Result<S extends ResponseEntity<BodyType>, F, BodyType> {

// MARK: - Construction

    Result(@NotNull S success) {
        // Init instance variables
        _state = State.SUCCESS;
        _success = success;
        _failure = null;
    }

    Result(@NotNull F failure) {
        // Init instance variables
        _state = State.FAILURE;
        _success = null;
        _failure = failure;
    }

// MARK: - Methods

    public @Nullable S value() {
        return (_state == State.SUCCESS) ? _success : null;
    }

    public @Nullable F error() {
        return (_state == State.FAILURE) ? _failure : null;
    }

    public boolean isSuccess() {
        return (_state == State.SUCCESS);
    }

// MARK: - Inner Types

    private enum State {
        SUCCESS, FAILURE
    }

// MARK: - Variables

    private final @NotNull State _state;

    private final @Nullable S _success;
    private final @Nullable F _failure;
}
