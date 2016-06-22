package com.roxiemobile.networkingapi.network.rest.converter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import com.roxiemobile.androidcommons.util.ArrayUtils;
import com.roxiemobile.androidcommons.util.LogUtils;
import com.roxiemobile.networkingapi.network.http.MediaType;
import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity;
import com.roxiemobile.networkingapi.network.rest.response.error.nested.ConversionException;
import com.roxiemobile.networkingapi.util.ResponseEntityUtils;

public class ImageConverter extends AbstractCallResultConverter<Bitmap>
{
// MARK: - Methods

    @Override
    public @NonNull ResponseEntity<Bitmap> convert(@NonNull ResponseEntity<byte[]> entity) throws ConversionException {
        ResponseEntity<Bitmap> newEntity;
        Bitmap newBody = null;

        try {
            byte[] body = entity.body();

            // Try to convert HTTP response to Bitmap
            if (!ArrayUtils.isEmpty(body)) {
                newBody = BitmapFactory.decodeByteArray(body, 0, body.length);
            }
        }
        catch (Exception ex) {
            LogUtils.e(TAG, ex);
            throw new ConversionException(entity, ex);
        }

        // Create new response entity
        newEntity = ResponseEntityUtils.copyWith(entity, newBody);
        return newEntity;
    }

    @Override
    protected @NonNull MediaType[] supportedMediaTypes() {
        return SUPPORTED_MEDIA_TYPES;
    }

// MARK: - Constants

    public static final String TAG = ImageConverter.class.getSimpleName();

    private static final MediaType[] SUPPORTED_MEDIA_TYPES = new MediaType[]{
            MediaType.IMAGE_JPEG,
            MediaType.IMAGE_PNG,
    };

}
