package com.roxiemobile.networkingapi.network.rest.converter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.roxiemobile.androidcommons.util.ArrayUtils;
import com.roxiemobile.networkingapi.network.http.MediaType;
import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity;
import com.roxiemobile.networkingapi.network.rest.response.error.nested.ConversionException;
import com.roxiemobile.networkingapi.util.ResponseEntityUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ImageConverter extends AbstractCallResultConverter<Bitmap> {

// MARK: - Methods

    @Override
    public @NotNull ResponseEntity<Bitmap> convert(@NotNull ResponseEntity<byte[]> responseEntity) throws ConversionException {

        @Nullable Bitmap newBody = null;
        try {

            @Nullable byte[] responseBody = responseEntity.body();

            // Try to convert HTTP response to Bitmap
            if (ArrayUtils.isNotEmpty(responseBody)) {
                newBody = BitmapFactory.decodeByteArray(responseBody, 0, responseBody.length);
            }
        }
        catch (Exception ex) {
            throw new ConversionException(responseEntity, ex);
        }

        // Create new response entity
        return ResponseEntityUtils.copyWith(responseEntity, newBody);
    }

    @Override
    protected @NotNull MediaType[] supportedMediaTypes() {
        return SUPPORTED_MEDIA_TYPES;
    }

// MARK: - Constants

    private static final @NotNull MediaType[] SUPPORTED_MEDIA_TYPES = new MediaType[]{
            MediaType.IMAGE_JPEG,
            MediaType.IMAGE_PNG,
    };
}
