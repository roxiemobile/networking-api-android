package com.roxiemobile.networkingapi.network.rest.converter;

import android.support.annotation.NonNull;

import com.roxiemobile.networkingapi.network.http.MediaType;
import com.roxiemobile.networkingapi.network.rest.response.BasicResponseEntity;
import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity;
import com.roxiemobile.networkingapi.network.rest.response.error.nested.ConversionException;

public class VoidConverter extends AbstractCallResultConverter<Void>
{
// MARK: - Methods

    @Override
    public @NonNull ResponseEntity<Void> convert(@NonNull ResponseEntity<byte[]> entity) throws ConversionException {
        return new BasicResponseEntity.Builder<Void>(entity, null).build();
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
