package com.roxiemobile.networkingapi.network.rest;

// Best way to return status flag and message from a method in Java
// @link http://stackoverflow.com/a/356293

import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity;

class Result<S extends ResponseEntity<BodyType>, F, BodyType>
{
// MARK: - Construction

    Result(S success) {
        // Init instance variables
        mState = State.SUCCESS;
        mSuccess = success;
        mFailure = null;
    }

    Result(F failure) {
        // Init instance variables
        mState = State.FAILURE;
        mSuccess = null;
        mFailure = failure;
    }

// MARK: - Methods

    public S value() {
        return (mState == State.SUCCESS) ? mSuccess : null;
    }

    public F error() {
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

    private final State mState;

    private final S mSuccess;
    private final F mFailure;
}
