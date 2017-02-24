package com.roxiemobile.networkingapi.network.rest.converter;

import android.support.annotation.NonNull;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.roxiemobile.androidcommons.data.mapper.DataMapper;
import com.roxiemobile.androidcommons.data.model.SerializableObject;
import com.roxiemobile.androidcommons.diagnostics.ExpectationException;
import com.roxiemobile.androidcommons.logging.Logger;
import com.roxiemobile.androidcommons.util.ArrayUtils;
import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity;
import com.roxiemobile.networkingapi.network.rest.response.error.nested.ConversionException;
import com.roxiemobile.networkingapi.util.ResponseEntityUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

public abstract class AbstractSerializableModelConverter<T extends SerializableObject>
        extends AbstractCallResultConverter<T>
{
// MARK: - Construction

    protected AbstractSerializableModelConverter(@NonNull Class<T> classOfT) {
        mClassOfType = classOfT;
    }

// MARK: - Methods

    @Override
    public @NonNull ResponseEntity<T> convert(@NonNull ResponseEntity<byte[]> entity) throws ConversionException {
        ResponseEntity<T> newEntity;
        T newBody = null;

        try {
            byte[] body = entity.body();

            // Try to convert HTTP response to POJO
            if (ArrayUtils.isNotEmpty(body)) {
                ByteArrayInputStream stream = new ByteArrayInputStream(body);
                newBody = DataMapper.fromJson(new InputStreamReader(stream), mClassOfType);
            }
        }
        catch (JsonSyntaxException | JsonIOException | ExpectationException ex) {
            Logger.e(TAG, ex);
            throw new ConversionException(entity, ex);
        }

        // Create new response entity
        newEntity = ResponseEntityUtils.copyWith(entity, newBody);
        return newEntity;
    }

// MARK: - Constants

    public static final String TAG = AbstractSerializableModelConverter.class.getSimpleName();

// MARK: - Variables

    private final Class<T> mClassOfType;
}
