package com.roxiemobile.networkingapi.network.rest.response.error;

import android.support.annotation.NonNull;

import com.annimon.stream.function.Consumer;
import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity;
import com.roxiemobile.networkingapi.network.rest.response.error.nested.ResponseEntityHolder;

import static com.roxiemobile.androidcommons.diagnostics.Require.requireNotNull;

public class ApplicationLayerError extends RestApiErrorImpl
{
// MARK: - Construction

    public ApplicationLayerError(@NonNull Throwable cause) {
        super(cause);
        requireNotNull(cause, "cause is null");
    }

// MARK: - Properties

    /**
     * Returns the type of an error.
     */
    public RestApiErrorType getType() {
        return RestApiErrorType.kApplicationLayer;
    }

// MARK: - Methods

    /**
     * Sends a printable representation of this {@code RestApiError}'s description
     * to the consumer.
     */
    @Override
    public void printErrorDescription(@NonNull Consumer<String> consumer) {
        Throwable cause = getCause();

        // Log nested error
        if (cause instanceof ResponseEntityHolder) {
            ResponseEntityHolder entityHolder = (ResponseEntityHolder) getCause();
            ResponseEntity<byte[]> entity = entityHolder.getResponseEntity();

            // Send error description to consumer
            consumer.accept("Request url: " + entity.uri());
            consumer.accept("Status: " + entity.status().value());

            // Good, but we need to go deeper :)
            entityHolder.printErrorDescription(consumer);
        }
        else {
            // Parent processing
            super.printErrorDescription(consumer);
        }
    }

}
