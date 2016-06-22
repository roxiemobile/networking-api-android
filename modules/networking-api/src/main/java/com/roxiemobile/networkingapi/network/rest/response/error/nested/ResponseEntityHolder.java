package com.roxiemobile.networkingapi.network.rest.response.error.nested;

import com.annimon.stream.function.Consumer;
import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity;

public interface ResponseEntityHolder
{
    /**
     * Returns the HTTP response entity.
     */
    ResponseEntity<byte[]> getResponseEntity();

    /**
     * Returns the response body as a byte array.
     */
    byte[] getResponseBodyAsBytes();

    /**
     * Returns the response body as a string.
     */
    String getResponseBodyAsString();

    /**
     * Sends a printable representation of this {@code RestApiError}'s description
     * to the consumer.
     */
    void printErrorDescription(Consumer<String> consumer);
}
