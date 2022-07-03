package com.roxiemobile.networkingapi.network.rest.converter;

import com.roxiemobile.androidcommons.data.Constants.Charsets;
import com.roxiemobile.androidcommons.logging.Logger;
import com.roxiemobile.androidcommons.util.ArrayUtils;
import com.roxiemobile.networkingapi.network.http.MediaType;
import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity;
import com.roxiemobile.networkingapi.network.rest.response.error.nested.ConversionException;
import com.roxiemobile.networkingapi.util.ResponseEntityUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.UnsupportedEncodingException;

public final class StringConverter extends AbstractCallResultConverter<String> {

// MARK: - Methods

    @Override
    public @NotNull ResponseEntity<String> convert(@NotNull ResponseEntity<byte[]> responseEntity) throws ConversionException {
        ResponseEntity<String> newEntity;
        String newBody = null;

        try {
            @Nullable byte[] responseBody = responseEntity.body();

            // Try to convert HTTP response to string
            if (ArrayUtils.isNotEmpty(responseBody)) {
                String charsetName = responseEntity.mediaType().getCharset(Charsets.UTF_8).name();
                newBody = new String(responseBody, charsetName);
            }
        }
        catch (UnsupportedEncodingException ex) {
            Logger.e(TAG, ex);
            throw new ConversionException(responseEntity, ex);
        }

        // Create new response entity
        newEntity = ResponseEntityUtils.copyWith(responseEntity, newBody);
        return newEntity;
    }

    @Override
    protected @NotNull MediaType[] supportedMediaTypes() {
        return SUPPORTED_MEDIA_TYPES;
    }

// MARK: - Constants

    public static final @NotNull String TAG = StringConverter.class.getSimpleName();

    private static final @NotNull MediaType[] SUPPORTED_MEDIA_TYPES = new MediaType[]{
            MediaType.ALL,
    };
}
