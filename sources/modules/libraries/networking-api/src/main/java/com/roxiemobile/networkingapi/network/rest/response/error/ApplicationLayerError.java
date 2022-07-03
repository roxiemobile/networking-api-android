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
            ResponseEntityHolder responseEntityHolder = (ResponseEntityHolder) getCause();
            ResponseEntity<byte[]> responseEntity = responseEntityHolder.getResponseEntity();

            // Send error description to consumer
            consumer.accept("Request link: " + responseEntity.getLink());
            consumer.accept("Status: " + responseEntity.getHttpStatus().value());

            // Good, but we need to go deeper :)
            responseEntityHolder.printErrorDescription(consumer);
        }
        else {
            // Parent processing
            super.printErrorDescription(consumer);
        }
    }
}
