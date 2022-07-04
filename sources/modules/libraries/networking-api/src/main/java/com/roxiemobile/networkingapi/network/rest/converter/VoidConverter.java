package com.roxiemobile.networkingapi.network.rest.converter;

import com.roxiemobile.networkingapi.network.http.MediaType;
import com.roxiemobile.networkingapi.network.rest.response.BasicResponseEntity;
import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity;
import com.roxiemobile.networkingapi.network.rest.response.error.nested.ConversionException;

import org.jetbrains.annotations.NotNull;

public class VoidConverter extends AbstractCallResultConverter<Void> {

// MARK: - Methods

    @Override
    public @NotNull ResponseEntity<Void> convert(@NotNull ResponseEntity<byte[]> entity) throws ConversionException {
        return new BasicResponseEntity.Builder<Void>(entity, null).build();
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
