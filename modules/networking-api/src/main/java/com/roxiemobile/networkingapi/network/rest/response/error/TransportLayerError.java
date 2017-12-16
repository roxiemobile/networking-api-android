package com.roxiemobile.networkingapi.network.rest.response.error;

import android.support.annotation.NonNull;

import com.roxiemobile.androidcommons.diagnostics.Guard;

public class TransportLayerError extends AbstractRestApiError
{
// MARK: - Construction

    public TransportLayerError(@NonNull Throwable cause) {
        super(cause);
        Guard.notNull(cause, "cause is null");
    }

// MARK: - Properties

    /**
     * Returns the type of an error.
     */
    public RestApiErrorType getType() {
        return RestApiErrorType.TRANSPORT_LAYER;
    }
}
