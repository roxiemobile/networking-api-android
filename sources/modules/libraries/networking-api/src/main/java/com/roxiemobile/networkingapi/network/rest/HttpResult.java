package com.roxiemobile.networkingapi.network.rest;

import com.roxiemobile.androidcommons.diagnostics.Guard;
import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity;

import org.jetbrains.annotations.NotNull;

public final class HttpResult extends Result<ResponseEntity<byte[]>, Throwable, byte[]> {

// MARK: - Construction

    private HttpResult(@NotNull ResponseEntity<byte[]> responseEntity) {
        super(responseEntity);
    }

    private HttpResult(@NotNull Throwable error) {
        super(error);
    }

// MARK: - Methods

    public static @NotNull HttpResult success(@NotNull ResponseEntity<byte[]> responseEntity) {
        Guard.notNull(responseEntity, "responseEntity is null");
        return new HttpResult(responseEntity);
    }

    public static @NotNull HttpResult failure(@NotNull Throwable error) {
        Guard.notNull(error, "error is null");
        return new HttpResult(error);
    }
}
