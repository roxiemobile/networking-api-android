package com.roxiemobile.networkingapi.network.rest.converter;

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.roxiemobile.androidcommons.data.Constants.Charsets;
import com.roxiemobile.androidcommons.util.ArrayUtils;
import com.roxiemobile.androidcommons.util.StringUtils;
import com.roxiemobile.networkingapi.network.http.MediaType;
import com.roxiemobile.networkingapi.network.rest.response.BasicResponseEntity;
import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity;
import com.roxiemobile.networkingapi.network.rest.response.error.nested.ConversionException;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.UnsupportedEncodingException;

public final class JsonObjectConverter extends AbstractCallResultConverter<JsonObject> {

// MARK: - Methods

    @Override
    public @NotNull ResponseEntity<JsonObject> convert(@NotNull ResponseEntity<byte[]> responseEntity) throws ConversionException {

        @Nullable JsonObject newBody = null;
        try {

            @Nullable byte[] responseBody = responseEntity.getBody();

            // Try to convert HTTP response to JSON object
            if (ArrayUtils.isNotEmpty(responseBody)) {

                @NotNull String charsetName = responseEntity.getMediaType().getCharset(Charsets.UTF_8).name();
                @NotNull String jsonString = new String(responseEntity.getBody(), charsetName).trim();

                if (StringUtils.isNotEmpty(jsonString)) {
                    newBody = new JsonParser().parse(jsonString).getAsJsonObject();
                }
            }
        }
        catch (UnsupportedEncodingException | JsonSyntaxException | JsonIOException | IllegalStateException ex) {
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
            MediaType.APPLICATION_JSON,
    };
}
