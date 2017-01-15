package com.roxiemobile.networkingapi.network.rest.response.error.nested;

import android.support.annotation.NonNull;

import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity;

public class ResponseException extends AbstractNestedError
{
// MARK: - Construction

    /**
     * Construct a new instance of {@code HttpResponseException} based on a {@link ResponseEntity}.
     */
    public ResponseException(@NonNull ResponseEntity<byte[]> entity) {
        super(entity);
    }

    /**
     * Construct a new instance of {@code HttpResponseException} based on a {@link ResponseEntity} and cause.
     */
    public ResponseException(@NonNull ResponseEntity<byte[]> entity, Throwable cause) {
        super(entity, cause);
    }

// MARK: - Constants

    private static final long serialVersionUID = 7228322733394988817L;

}
