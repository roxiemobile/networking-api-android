package com.roxiemobile.networkingapi.network.rest.response;

import com.roxiemobile.networkingapi.network.http.HttpStatus;
import com.roxiemobile.networkingapi.network.http.MediaType;
import com.roxiemobile.networkingapi.network.rest.request.RequestEntity;

import org.jetbrains.annotations.NotNull;

public interface ResponseEntity<T> extends RequestEntity<T> {

// MARK: - Properties

    /**
     * TODO
     */
    @NotNull HttpStatus getHttpStatus();

    /**
     * TODO
     */
    @NotNull MediaType getMediaType();
}
