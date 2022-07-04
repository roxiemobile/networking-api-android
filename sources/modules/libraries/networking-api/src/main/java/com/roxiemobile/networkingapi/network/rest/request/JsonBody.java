package com.roxiemobile.networkingapi.network.rest.request;

import com.google.gson.JsonElement;
import com.roxiemobile.androidcommons.data.Constants.Charsets;
import com.roxiemobile.androidcommons.data.mapper.DataMapper;
import com.roxiemobile.networkingapi.network.http.MediaType;
import com.roxiemobile.networkingapi.network.rest.HttpBody;

import org.jetbrains.annotations.NotNull;

public final class JsonBody implements HttpBody {

// MARK: - Construction

    public JsonBody(@NotNull JsonElement body) {
        mBody = body;
    }

// MARK: - Methods

    @Override
    public @NotNull MediaType mediaType() {
        return MEDIA_TYPE;
    }

    @Override
    public @NotNull byte[] body() {
        return (mBody != null) ? DataMapper.toByteArray(mBody) : null;
    }

// MARK: - Constants

    private final static @NotNull MediaType MEDIA_TYPE =
            MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE + "; charset=" + Charsets.UTF_8.name());

// MARK: - Variables

    private final @NotNull JsonElement mBody;
}
