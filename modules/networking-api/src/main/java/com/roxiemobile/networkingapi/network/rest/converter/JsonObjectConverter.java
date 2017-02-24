package com.roxiemobile.networkingapi.network.rest.converter;

import android.support.annotation.NonNull;

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.roxiemobile.androidcommons.logging.Logger;
import com.roxiemobile.androidcommons.util.ArrayUtils;
import com.roxiemobile.androidcommons.util.StringUtils;
import com.roxiemobile.networkingapi.network.NetworkConfig.DefaultCharset;
import com.roxiemobile.networkingapi.network.http.MediaType;
import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity;
import com.roxiemobile.networkingapi.network.rest.response.error.nested.ConversionException;
import com.roxiemobile.networkingapi.util.ResponseEntityUtils;

import java.io.UnsupportedEncodingException;

public class JsonObjectConverter extends AbstractCallResultConverter<JsonObject>
{
// MARK: - Methods

    @Override
    public @NonNull ResponseEntity<JsonObject> convert(@NonNull ResponseEntity<byte[]> entity) throws ConversionException {
        ResponseEntity<JsonObject> newEntity;
        JsonObject newBody = null;

        try {
            byte[] body = entity.body();

            // Try to convert HTTP response to JSON object
            if (ArrayUtils.isNotEmpty(body)) {

                String charsetName = entity.mediaType().getCharset(DefaultCharset.UTF_8).name();
                String json = new String(entity.body(), charsetName).trim();

                if (StringUtils.isNotEmpty(json)) {
                    newBody = new JsonParser().parse(json).getAsJsonObject();
                }
            }
        }
        catch (UnsupportedEncodingException | JsonSyntaxException | JsonIOException | IllegalStateException ex) {
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

    public static final String TAG = JsonObjectConverter.class.getSimpleName();

    private static final MediaType[] SUPPORTED_MEDIA_TYPES = new MediaType[]{
            MediaType.APPLICATION_JSON,
    };
}
