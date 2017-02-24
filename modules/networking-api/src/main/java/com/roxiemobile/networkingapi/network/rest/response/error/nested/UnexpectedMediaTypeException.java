package com.roxiemobile.networkingapi.network.rest.response.error.nested;

import android.support.annotation.NonNull;

import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity;

public class UnexpectedMediaTypeException extends AbstractNestedError
{
// MARK: - Construction

    /**
     * Construct a new instance of {@code UnexpectedMediaTypeException} based on a {@link ResponseEntity}.
     */
    public UnexpectedMediaTypeException(@NonNull ResponseEntity<byte[]> entity) {
        super(entity);
    }

    /**
     * Construct a new instance of {@code UnexpectedMediaTypeException} based on a {@link ResponseEntity} and cause.
     */
    public UnexpectedMediaTypeException(@NonNull ResponseEntity<byte[]> entity, Throwable cause) {
        super(entity, cause);
    }

// MARK: - Constants

    private static final long serialVersionUID = -3309836591177713721L;
}
