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
        mState = State.SUCCESS;
        mSuccess = success;
        mFailure = null;
    }

    Result(@NotNull F failure) {
        // Init instance variables
        mState = State.FAILURE;
        mSuccess = null;
        mFailure = failure;
    }

// MARK: - Methods

    public @Nullable S value() {
        return (mState == State.SUCCESS) ? mSuccess : null;
    }

    public @Nullable F error() {
        return (mState == State.FAILURE) ? mFailure : null;
    }

    public boolean isSuccess() {
        return (mState == State.SUCCESS);
    }

// MARK: - Inner Types

    private enum State {
        SUCCESS, FAILURE
    }

// MARK: - Variables

    private final @NotNull State mState;

    private final @Nullable S mSuccess;
    private final @Nullable F mFailure;
}
