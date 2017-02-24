package com.roxiemobile.networkingapi.network.rest.converter;

import android.support.annotation.NonNull;

import com.roxiemobile.networkingapi.network.http.MediaType;
import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity;
import com.roxiemobile.networkingapi.network.rest.response.error.nested.ConversionException;

public class PassThroughConverter extends AbstractCallResultConverter<byte[]>
{
// MARK: - Methods

    @Override
    public @NonNull ResponseEntity<byte[]> convert(@NonNull ResponseEntity<byte[]> entity) throws ConversionException {
        return entity;
    }

    @Override
    protected @NonNull MediaType[] supportedMediaTypes() {
        return SUPPORTED_MEDIA_TYPES;
    }

// MARK: - Constants

    private static final MediaType[] SUPPORTED_MEDIA_TYPES = new MediaType[]{
            MediaType.ALL
    };
}
