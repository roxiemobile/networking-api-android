package com.roxiemobile.networkingapi.network.rest.response.error.nested;

import com.annimon.stream.function.Consumer;
import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ResponseEntityHolder {

    /**
     * Returns the HTTP response entity.
     */
    @NotNull ResponseEntity<byte[]> getResponseEntity();

    /**
     * Returns the response body as a byte array.
     */
    @Nullable byte[] getResponseBodyAsBytes();

    /**
     * Returns the response body as a string.
     */
    @Nullable String getResponseBodyAsString();

    /**
     * Sends a printable representation of this {@code RestApiError}'s description
     * to the consumer.
     */
    void printErrorDescription(@NotNull Consumer<String> consumer);
}
