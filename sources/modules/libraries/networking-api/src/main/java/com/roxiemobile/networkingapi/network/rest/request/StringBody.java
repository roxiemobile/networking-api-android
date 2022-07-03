package com.roxiemobile.networkingapi.network.rest.request;

import com.roxiemobile.androidcommons.data.Constants.Charsets;
import com.roxiemobile.networkingapi.network.http.MediaType;
import com.roxiemobile.networkingapi.network.rest.HttpBody;

import org.jetbrains.annotations.NotNull;

public final class StringBody implements HttpBody {

// MARK: - Construction

    public StringBody(@NotNull String body) {
        mBody = body;
    }

// MARK: - Methods

    @Override
    public @NotNull MediaType getMediaType() {
        return MEDIA_TYPE;
    }

    @Override
    public @NotNull byte[] getBody() {
        return (mBody != null) ? mBody.getBytes(Charsets.UTF_8) : null;
    }

// MARK: - Constants

    private final static @NotNull MediaType MEDIA_TYPE =
            MediaType.valueOf(MediaType.TEXT_PLAIN_VALUE + "; charset=" + Charsets.UTF_8.name());

// MARK: - Variables

    private final @NotNull String mBody;
}
