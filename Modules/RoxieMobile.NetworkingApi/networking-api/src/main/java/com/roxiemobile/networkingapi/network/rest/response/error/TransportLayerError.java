package com.roxiemobile.networkingapi.network.rest.response.error;

import com.roxiemobile.androidcommons.diagnostics.Guard;

import org.jetbrains.annotations.NotNull;

public class TransportLayerError extends AbstractRestApiError
{
// MARK: - Construction

    public TransportLayerError(@NotNull Throwable cause) {
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
