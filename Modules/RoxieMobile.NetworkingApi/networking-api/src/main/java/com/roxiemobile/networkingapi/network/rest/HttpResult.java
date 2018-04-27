package com.roxiemobile.networkingapi.network.rest;

import com.roxiemobile.androidcommons.diagnostics.Guard;
import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity;

import org.jetbrains.annotations.NotNull;

public class HttpResult extends Result<ResponseEntity<byte[]>, Throwable, byte[]>
{
// MARK: - Construction

    private HttpResult(ResponseEntity<byte[]> success) {
        super(success);
    }

    private HttpResult(Throwable failure) {
        super(failure);
    }

// MARK: - Methods

    public static HttpResult success(@NotNull ResponseEntity<byte[]> response) {
        Guard.notNull(response, "response is null");
        return new HttpResult(response);
    }

    public static HttpResult failure(@NotNull Throwable error) {
        Guard.notNull(error, "error is null");
        return new HttpResult(error);
    }
}
