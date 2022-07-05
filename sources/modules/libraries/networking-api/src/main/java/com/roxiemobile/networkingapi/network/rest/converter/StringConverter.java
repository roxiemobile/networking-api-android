package com.roxiemobile.networkingapi.network.rest.converter;

import com.roxiemobile.androidcommons.data.Constants.Charsets;
import com.roxiemobile.androidcommons.util.ArrayUtils;
import com.roxiemobile.networkingapi.network.http.MediaType;
import com.roxiemobile.networkingapi.network.rest.response.BasicResponseEntity;
import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity;
import com.roxiemobile.networkingapi.network.rest.response.error.nested.ConversionException;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.UnsupportedEncodingException;

public final class StringConverter extends AbstractCallResultConverter<String> {

// MARK: - Methods

    @Override
    public @NotNull ResponseEntity<String> convert(@NotNull ResponseEntity<byte[]> responseEntity) throws ConversionException {

        @Nullable String newBody = null;
        try {

            @Nullable byte[] responseBody = responseEntity.getBody();

            // Try to convert HTTP response to string
            if (ArrayUtils.isNotEmpty(responseBody)) {
                @NotNull String charsetName = responseEntity.getMediaType().getCharset(Charsets.UTF_8).name();
                newBody = new String(responseBody, charsetName);
            }
        }
        catch (UnsupportedEncodingException ex) {
            throw new ConversionException(responseEntity, ex);
        }

        // Create new response entity
        return BasicResponseEntity.of(responseEntity, newBody);
    }

    @Override
    protected @NotNull MediaType[] supportedMediaTypes() {
        return SUPPORTED_MEDIA_TYPES;
    }

// MARK: - Constants

    private static final @NotNull MediaType[] SUPPORTED_MEDIA_TYPES = new MediaType[]{
            MediaType.ALL,
    };
}
