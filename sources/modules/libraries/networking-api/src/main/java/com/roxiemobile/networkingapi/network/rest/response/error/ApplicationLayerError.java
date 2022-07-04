package com.roxiemobile.networkingapi.network.rest.response.error;

import com.annimon.stream.function.Consumer;
import com.roxiemobile.androidcommons.diagnostics.Guard;
import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity;
import com.roxiemobile.networkingapi.network.rest.response.error.nested.ResponseEntityHolder;

import org.jetbrains.annotations.NotNull;

public final class ApplicationLayerError extends AbstractRestApiError {

// MARK: - Construction

    public ApplicationLayerError(@NotNull Throwable cause) {
        super(cause);
        Guard.notNull(cause, "cause is null");
    }

// MARK: - Properties

    /**
     * Returns the type of an error.
     */
    public @NotNull RestApiErrorType getType() {
        return RestApiErrorType.APPLICATION_LAYER;
    }

// MARK: - Methods

    /**
     * Sends a printable representation of this {@code RestApiError}'s description
     * to the consumer.
     */
    @Override
    public void printErrorDescription(@NotNull Consumer<String> consumer) {
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
