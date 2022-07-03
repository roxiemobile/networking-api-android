package com.roxiemobile.networkingapi.network.rest.response.error.nested;

import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ResponseException extends AbstractNestedError {

// MARK: - Construction

    /**
     * Construct a new instance of {@code HttpResponseException} based on a {@link ResponseEntity}.
     */
    public ResponseException(@NotNull ResponseEntity<byte[]> responseEntity) {
        super(responseEntity);
    }

    /**
     * Construct a new instance of {@code HttpResponseException} based on a {@link ResponseEntity} and cause.
     */
    public ResponseException(@NotNull ResponseEntity<byte[]> responseEntity, @Nullable Throwable cause) {
        super(responseEntity, cause);
    }

// MARK: - Constants

    private static final long serialVersionUID = 7228322733394988817L;
}
