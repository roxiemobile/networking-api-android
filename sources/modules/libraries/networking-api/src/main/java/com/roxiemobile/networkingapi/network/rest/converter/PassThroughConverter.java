package com.roxiemobile.networkingapi.network.rest.converter;

import com.roxiemobile.networkingapi.network.http.MediaType;
import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity;
import com.roxiemobile.networkingapi.network.rest.response.error.nested.ConversionException;

import org.jetbrains.annotations.NotNull;

public final class PassThroughConverter extends AbstractCallResultConverter<byte[]> {

// MARK: - Methods

    @Override
    public @NotNull ResponseEntity<byte[]> convert(@NotNull ResponseEntity<byte[]> entity) throws ConversionException {
        return entity;
    }

    @Override
    protected @NotNull MediaType[] supportedMediaTypes() {
        return SUPPORTED_MEDIA_TYPES;
    }

// MARK: - Constants

    private static final @NotNull MediaType[] SUPPORTED_MEDIA_TYPES = new MediaType[]{
            MediaType.ALL
    };
}
