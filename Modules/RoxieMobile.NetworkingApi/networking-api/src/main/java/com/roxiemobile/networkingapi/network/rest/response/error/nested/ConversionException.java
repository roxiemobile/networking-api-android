package com.roxiemobile.networkingapi.network.rest.response.error.nested;

import android.support.annotation.NonNull;

import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity;

public class ConversionException extends AbstractNestedError
{
// MARK: - Construction

    /**
     * Construct a new instance of {@code ConversionException} based on a {@link ResponseEntity}.
     */
    public ConversionException(@NonNull ResponseEntity<byte[]> entity) {
        super(entity);
    }

    /**
     * Construct a new instance of {@code ConversionException} based on a {@link ResponseEntity} and cause.
     */
    public ConversionException(@NonNull ResponseEntity<byte[]> entity, Throwable cause) {
        super(entity, cause);
    }

// MARK: - Constants

    private static final long serialVersionUID = -2046452406935471379L;
}
