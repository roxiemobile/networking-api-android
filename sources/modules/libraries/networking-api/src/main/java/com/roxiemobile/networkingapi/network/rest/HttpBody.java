package com.roxiemobile.networkingapi.network.rest;

import com.roxiemobile.networkingapi.network.http.MediaType;

import org.jetbrains.annotations.NotNull;

public interface HttpBody {

// MARK: - Properties

    /**
     * TODO
     */
    @NotNull MediaType getMediaType();

    /**
     * TODO
     */
    @NotNull byte[] getBody();
}
