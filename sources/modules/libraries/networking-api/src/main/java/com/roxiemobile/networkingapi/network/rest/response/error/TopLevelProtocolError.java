package com.roxiemobile.networkingapi.network.rest.response.error;

import com.roxiemobile.androidcommons.diagnostics.Guard;
import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity;

import org.jetbrains.annotations.NotNull;

public abstract class TopLevelProtocolError<T> extends AbstractRestApiError {

// MARK: - Construction

    public TopLevelProtocolError(@NotNull ResponseEntity<T> responseEntity) {
        super(null);
        Guard.notNull(responseEntity, "responseEntity is null");

        // Init instance variables
        mResponseEntity = responseEntity;
    }

// MARK: - Properties

    /**
     * Returns the type of an error.
     */
    @Override
    public @NotNull RestApiErrorType getType() {
        return RestApiErrorType.TOP_LEVEL_PROTOCOL;
    }

    /**
     * Returns the response entity.
     */
    public @NotNull ResponseEntity<T> getResponseEntity() {
        return mResponseEntity;
    }

// MARK: - Variables

    private final @NotNull ResponseEntity<T> mResponseEntity;
}
