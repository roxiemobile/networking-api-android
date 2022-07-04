package com.roxiemobile.networkingapi.network.rest;

import com.roxiemobile.androidcommons.diagnostics.Guard;
import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity;
import com.roxiemobile.networkingapi.network.rest.response.RestApiError;

import org.jetbrains.annotations.NotNull;

public class CallResult<T> extends Result<ResponseEntity<T>, RestApiError, T> {

// MARK: - Construction

    private CallResult(@NotNull ResponseEntity<T> success) {
        super(success);
    }

    private CallResult(@NotNull RestApiError failure) {
        super(failure);
    }

// MARK: - Methods

    public static @NotNull <T> CallResult<T> success(@NotNull ResponseEntity<T> entity) {
        Guard.notNull(entity, "entity is null");
        return new CallResult<>(entity);
    }

    public static @NotNull <T> CallResult<T> failure(@NotNull RestApiError error) {
        Guard.notNull(error, "error is null");
        return new CallResult<>(error);
    }
}
