package com.roxiemobile.networkingapi.network.rest.converter;

import android.support.annotation.NonNull;

import com.roxiemobile.androidcommons.logging.Logger;
import com.roxiemobile.androidcommons.util.ArrayUtils;
import com.roxiemobile.networkingapi.network.NetworkConfig.DefaultCharset;
import com.roxiemobile.networkingapi.network.http.MediaType;
import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity;
import com.roxiemobile.networkingapi.network.rest.response.error.nested.ConversionException;
import com.roxiemobile.networkingapi.util.ResponseEntityUtils;

import java.io.UnsupportedEncodingException;

public class StringConverter extends AbstractCallResultConverter<String>
{
// MARK: - Methods

    @Override
    public @NonNull ResponseEntity<String> convert(@NonNull ResponseEntity<byte[]> entity) throws ConversionException {
        ResponseEntity<String> newEntity;
        String newBody = null;

        try {
            byte[] body = entity.body();

            // Try to convert HTTP response to string
            if (ArrayUtils.isNotEmpty(body)) {
                String charsetName = entity.mediaType().getCharset(DefaultCharset.UTF_8).name();
                newBody = new String(body, charsetName);
            }
        }
        catch (UnsupportedEncodingException ex) {
            Logger.e(TAG, ex);
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

    public static final String TAG = StringConverter.class.getSimpleName();

    private static final MediaType[] SUPPORTED_MEDIA_TYPES = new MediaType[]{
            MediaType.ALL
    };

}
