package com.roxiemobile.networkingapi.network.rest.response.error.nested;

import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class UnexpectedMediaTypeException extends AbstractNestedError {

// MARK: - Construction

    /**
     * Construct a new instance of {@code UnexpectedMediaTypeException} based on a {@link ResponseEntity}.
     */
    public UnexpectedMediaTypeException(@NotNull ResponseEntity<byte[]> entity) {
        super(entity);
    }

    /**
     * Construct a new instance of {@code UnexpectedMediaTypeException} based on a {@link ResponseEntity} and cause.
     */
    public UnexpectedMediaTypeException(@NotNull ResponseEntity<byte[]> entity, @Nullable Throwable cause) {
        super(entity, cause);
    }

// MARK: - Constants

    private static final long serialVersionUID = -3309836591177713721L;
}
