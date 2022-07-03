package com.roxiemobile.networkingapi.network.rest.request;

import com.roxiemobile.networkingapi.network.http.MediaType;
import com.roxiemobile.networkingapi.network.rest.HttpBody;

import org.jetbrains.annotations.NotNull;

public final class VoidBody implements HttpBody {

// MARK: - Construction

    private VoidBody() {
        throw new UnsupportedOperationException();
    }

// MARK: - Methods

    @Override
    public @NotNull MediaType getMediaType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull byte[] getBody() {
        throw new UnsupportedOperationException();
    }
}
