package com.roxiemobile.networkingapi.network.rest;

import com.roxiemobile.androidcommons.diagnostics.Guard;
import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity;

import org.jetbrains.annotations.NotNull;

public final class HttpResult extends Result<ResponseEntity<byte[]>, Throwable, byte[]> {

// MARK: - Construction

    private HttpResult(@NotNull ResponseEntity<byte[]> success) {
        super(success);
    }

    private HttpResult(@NotNull Throwable failure) {
        super(failure);
    }

// MARK: - Methods

    public static @NotNull HttpResult success(@NotNull ResponseEntity<byte[]> response) {
        Guard.notNull(response, "response is null");
        return new HttpResult(response);
    }

    public static @NotNull HttpResult failure(@NotNull Throwable error) {
        Guard.notNull(error, "error is null");
        return new HttpResult(error);
    }
}
