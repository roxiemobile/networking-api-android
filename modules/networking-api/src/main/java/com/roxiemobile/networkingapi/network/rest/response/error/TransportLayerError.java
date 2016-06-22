package com.roxiemobile.networkingapi.network.rest.response.error;

import android.support.annotation.NonNull;

import static com.roxiemobile.androidcommons.util.AssertUtils.assertNotNull;

public class TransportLayerError extends RestApiErrorImpl
{
// MARK: - Construction

    public TransportLayerError(@NonNull Throwable cause) {
        super(cause);
        assertNotNull(cause, "cause == null");
    }

// MARK: - Properties

    /**
     * Returns the type of an error.
     */
    public RestApiErrorType getType() {
        return RestApiErrorType.kTransportLayer;
    }

}
