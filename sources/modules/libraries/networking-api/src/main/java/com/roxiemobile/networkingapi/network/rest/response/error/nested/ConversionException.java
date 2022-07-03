package com.roxiemobile.networkingapi.network.rest.response.error.nested;

import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ConversionException extends AbstractNestedError {

// MARK: - Construction

    /**
     * Construct a new instance of {@code ConversionException} based on a {@link ResponseEntity}.
     */
    public ConversionException(@NotNull ResponseEntity<byte[]> responseEntity) {
        super(responseEntity);
    }

    /**
     * Construct a new instance of {@code ConversionException} based on a {@link ResponseEntity} and cause.
     */
    public ConversionException(@NotNull ResponseEntity<byte[]> responseEntity, @Nullable Throwable cause) {
        super(responseEntity, cause);
    }

// MARK: - Constants

    private static final long serialVersionUID = -2046452406935471379L;
}
