package com.roxiemobile.networkingapi.network.rest.request;

import com.roxiemobile.networkingapi.network.http.MediaType;
import com.roxiemobile.networkingapi.network.rest.HttpBody;

import org.jetbrains.annotations.NotNull;

public final class ByteArrayBody implements HttpBody {

// MARK: - Construction

    public ByteArrayBody(@NotNull byte[] body, @NotNull MediaType mediaType) {
        _body = body;
        _mediaType = mediaType;
    }

    public ByteArrayBody(@NotNull byte[] body) {
        this(body, MediaType.APPLICATION_OCTET_STREAM);
    }

    public ByteArrayBody() {
        this(EMPTY_ARRAY);
    }

// MARK: - Methods

    @Override
    public @NotNull MediaType getMediaType() {
        return _mediaType;
    }

    @Override
    public @NotNull byte[] getBody() {
        return _body;
    }

// MARK: - Constants

    private static final @NotNull byte[] EMPTY_ARRAY = new byte[]{};

// MARK: - Variables

    private final @NotNull MediaType _mediaType;

    private final @NotNull byte[] _body;
}
