package com.roxiemobile.networkingapi.network.rest;

import com.roxiemobile.networkingapi.network.http.MediaType;

import org.jetbrains.annotations.NotNull;

public interface HttpBody {

    /**
     * TODO
     */
    @NotNull MediaType mediaType();

    /**
     * TODO
     */
    @NotNull byte[] body();
}
