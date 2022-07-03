package com.roxiemobile.networkingapi.network.rest.response.error.nested;

import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ConversionException extends AbstractNestedError {

// MARK: - Construction

    /**
     * Construct a new instance of {@code ConversionException} based on a {@link ResponseEntity}.
     */
    public ConversionException(@NotNull ResponseEntity<byte[]> entity) {
        super(entity);
    }

    /**
     * Construct a new instance of {@code ConversionException} based on a {@link ResponseEntity} and cause.
     */
    public ConversionException(@NotNull ResponseEntity<byte[]> entity, @Nullable Throwable cause) {
        super(entity, cause);
    }

// MARK: - Constants

    private static final long serialVersionUID = -2046452406935471379L;
}
