package com.roxiemobile.networkingapi.network.rest.response.error;

import com.roxiemobile.androidcommons.diagnostics.Guard;
import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity;

import org.jetbrains.annotations.NotNull;

public abstract class TopLevelProtocolError<T> extends AbstractRestApiError
{
// MARK: - Construction

    public TopLevelProtocolError(@NotNull ResponseEntity<T> entity) {
        super(null);
        Guard.notNull(entity, "entity is null");

        // Init instance variables
        mResponseEntity = entity;
    }

// MARK: - Properties

    /**
     * Returns the type of an error.
     */
    @Override
    public RestApiErrorType getType() {
        return RestApiErrorType.TOP_LEVEL_PROTOCOL;
    }

    /**
     * Returns the response entity.
     */
    public @NotNull ResponseEntity<T> getResponseEntity() {
        return mResponseEntity;
    }

// MARK: - Variables

    private final ResponseEntity<T> mResponseEntity;
}
