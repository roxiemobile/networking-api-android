package com.roxiemobile.networkingapi.network.rest.request;

import com.roxiemobile.networkingapi.network.http.MediaType;
import com.roxiemobile.networkingapi.network.rest.HttpBody;

import org.jetbrains.annotations.NotNull;

public final class VoidBody implements HttpBody {

// MARK: - Construction

    private VoidBody() {
        // Do nothing
    }

// MARK: - Methods

    @Override
    public @NotNull MediaType mediaType() {
        return null;
    }

    @Override
    public @NotNull byte[] body() {
        return null;
    }
}
