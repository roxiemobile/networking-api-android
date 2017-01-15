package com.roxiemobile.networkingapi.network.rest.response.error;

import android.support.annotation.NonNull;

import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity;

import static com.roxiemobile.androidcommons.diagnostics.Require.requireNotNull;

public abstract class TopLevelProtocolError<T> extends AbstractRestApiError
{
// MARK: - Construction

    public TopLevelProtocolError(@NonNull ResponseEntity<T> entity) {
        super(null);
        requireNotNull(entity, "entity is null");

        // Init instance variables
        mResponseEntity = entity;
    }

// MARK: - Properties

    /**
     * Returns the type of an error.
     */
    @Override
    public RestApiErrorType getType() {
        return RestApiErrorType.kTopLevelProtocol;
    }

    /**
     * Returns the response entity.
     */
    public @NonNull ResponseEntity<T> getResponseEntity() {
        return mResponseEntity;
    }

// MARK: - Variables

    private final ResponseEntity<T> mResponseEntity;

}
