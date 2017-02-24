package com.roxiemobile.networkingapi.network.rest;

import android.support.annotation.NonNull;

import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity;

import static com.roxiemobile.androidcommons.diagnostics.Require.requireNotNull;

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

    public static HttpResult success(@NonNull ResponseEntity<byte[]> response) {
        requireNotNull(response, "response is null");
        return new HttpResult(response);
    }

    public static HttpResult failure(@NonNull Throwable error) {
        requireNotNull(error, "error is null");
        return new HttpResult(error);
    }
}
