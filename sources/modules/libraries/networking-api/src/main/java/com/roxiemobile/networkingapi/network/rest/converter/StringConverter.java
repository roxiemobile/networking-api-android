package com.roxiemobile.networkingapi.network.rest.converter;

import com.roxiemobile.androidcommons.data.Constants.Charsets;
import com.roxiemobile.androidcommons.logging.Logger;
import com.roxiemobile.androidcommons.util.ArrayUtils;
import com.roxiemobile.networkingapi.network.http.MediaType;
import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity;
import com.roxiemobile.networkingapi.network.rest.response.error.nested.ConversionException;
import com.roxiemobile.networkingapi.util.ResponseEntityUtils;

import org.jetbrains.annotations.NotNull;

import java.io.UnsupportedEncodingException;

public class StringConverter extends AbstractCallResultConverter<String> {

// MARK: - Methods

    @Override
    public @NotNull ResponseEntity<String> convert(@NotNull ResponseEntity<byte[]> entity) throws ConversionException {
        ResponseEntity<String> newEntity;
        String newBody = null;

        try {
            byte[] body = entity.body();

            // Try to convert HTTP response to string
            if (ArrayUtils.isNotEmpty(body)) {
                String charsetName = entity.mediaType().getCharset(Charsets.UTF_8).name();
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
    protected @NotNull MediaType[] supportedMediaTypes() {
        return SUPPORTED_MEDIA_TYPES;
    }

// MARK: - Constants

    public static final String TAG = StringConverter.class.getSimpleName();

    private static final MediaType[] SUPPORTED_MEDIA_TYPES = new MediaType[]{
            MediaType.ALL
    };
}
