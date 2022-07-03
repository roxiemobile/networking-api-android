package com.roxiemobile.networkingapi.network.rest;

import com.roxiemobile.androidcommons.diagnostics.Guard;
import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity;
import com.roxiemobile.networkingapi.network.rest.response.RestApiError;

import org.jetbrains.annotations.NotNull;

public final class CallResult<T> extends Result<ResponseEntity<T>, RestApiError, T> {

// MARK: - Construction

    private CallResult(@NotNull ResponseEntity<T> responseEntity) {
        super(responseEntity);
    }

    private CallResult(@NotNull RestApiError error) {
        super(error);
    }

// MARK: - Methods

    public static @NotNull <T> CallResult<T> success(@NotNull ResponseEntity<T> responseEntity) {
        Guard.notNull(responseEntity, "responseEntity is null");
        return new CallResult<>(responseEntity);
    }

    public static @NotNull <T> CallResult<T> failure(@NotNull RestApiError error) {
        Guard.notNull(error, "error is null");
        return new CallResult<>(error);
    }
}
