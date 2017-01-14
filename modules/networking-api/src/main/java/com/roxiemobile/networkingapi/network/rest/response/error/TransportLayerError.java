package com.roxiemobile.networkingapi.network.rest.response.error;

import android.support.annotation.NonNull;

import static com.roxiemobile.androidcommons.diagnostics.Require.requireNotNull;

public class TransportLayerError extends RestApiErrorImpl
{
// MARK: - Construction

    public TransportLayerError(@NonNull Throwable cause) {
        super(cause);
        requireNotNull(cause, "cause is null");
    }

// MARK: - Properties

    /**
     * Returns the type of an error.
     */
    public RestApiErrorType getType() {
        return RestApiErrorType.kTransportLayer;
    }

}
