package com.roxiemobile.networkingapi.network.rest.request;

import com.roxiemobile.androidcommons.diagnostics.Guard;
import com.roxiemobile.networkingapi.network.http.MediaType;
import com.roxiemobile.networkingapi.network.rest.HttpBody;

import org.jetbrains.annotations.NotNull;

public final class ByteArrayBody implements HttpBody {

// MARK: - Construction

    public ByteArrayBody(@NotNull byte[] body, @NotNull MediaType mediaType) {
        Guard.notNull(body, "body is null");
        Guard.notNull(mediaType, "mediaType is null");

        // Init instance variables
        mBody = body;
        mMediaType = mediaType;
    }

    public ByteArrayBody(@NotNull byte[] body) {
        this(body, MediaType.APPLICATION_OCTET_STREAM);
    }

    public ByteArrayBody() {
        this(EMPTY_ARRAY);
    }

// MARK: - Methods

    @Override
    public @NotNull MediaType mediaType() {
        return mMediaType;
    }

    @Override
    public @NotNull byte[] body() {
        return mBody;
    }

// MARK: - Constants

    private static final @NotNull byte[] EMPTY_ARRAY = new byte[]{};

// MARK: - Variables

    private final @NotNull MediaType mMediaType;

    private final @NotNull byte[] mBody;
}
