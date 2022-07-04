package com.roxiemobile.networkingapi.network.rest.response;

import com.annimon.stream.function.Consumer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface RestApiError {

// MARK: - Properties

    /**
     * Returns the type of an error.
     */
    @NotNull RestApiErrorType getType();

    /**
     * Returns the cause of this {@code RestApiError}, or {@code null} if there is no cause.
     */
    @Nullable Throwable getCause();

// MARK: - Methods

    /**
     * Sends a printable representation of this {@code RestApiError}'s description
     * to the consumer.
     */
    void printErrorDescription(@NotNull Consumer<String> consumer);

// MARK: - Inner Types

    /**
     * The type of an error.
     */
    enum RestApiErrorType {
        TRANSPORT_LAYER, APPLICATION_LAYER, TOP_LEVEL_PROTOCOL
    }
}
