package com.roxiemobile.networkingapi.network.rest.converter;

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.roxiemobile.androidcommons.data.Constants.Charsets;
import com.roxiemobile.androidcommons.logging.Logger;
import com.roxiemobile.androidcommons.util.ArrayUtils;
import com.roxiemobile.androidcommons.util.StringUtils;
import com.roxiemobile.networkingapi.network.http.MediaType;
import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity;
import com.roxiemobile.networkingapi.network.rest.response.error.nested.ConversionException;
import com.roxiemobile.networkingapi.util.ResponseEntityUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.UnsupportedEncodingException;

public final class JsonObjectConverter extends AbstractCallResultConverter<JsonObject> {

// MARK: - Methods

    @Override
    public @NotNull ResponseEntity<JsonObject> convert(@NotNull ResponseEntity<byte[]> responseEntity) throws ConversionException {
        ResponseEntity<JsonObject> newEntity;
        JsonObject newBody = null;

        try {
            @Nullable byte[] responseBody = responseEntity.body();

            // Try to convert HTTP response to JSON object
            if (ArrayUtils.isNotEmpty(responseBody)) {

                String charsetName = responseEntity.mediaType().getCharset(Charsets.UTF_8).name();
                String jsonString = new String(responseEntity.body(), charsetName).trim();

                if (StringUtils.isNotEmpty(jsonString)) {
                    newBody = new JsonParser().parse(jsonString).getAsJsonObject();
                }
            }
        }
        catch (UnsupportedEncodingException | JsonSyntaxException | JsonIOException | IllegalStateException ex) {
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

    public static final @NotNull String TAG = JsonObjectConverter.class.getSimpleName();

    private static final @NotNull MediaType[] SUPPORTED_MEDIA_TYPES = new MediaType[]{
            MediaType.APPLICATION_JSON,
    };
}
