package com.roxiemobile.networkingapi.network.rest.converter;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.roxiemobile.androidcommons.data.mapper.DataMapper;
import com.roxiemobile.androidcommons.data.model.ValidatableModel;
import com.roxiemobile.androidcommons.logging.Logger;
import com.roxiemobile.androidcommons.util.ArrayUtils;
import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity;
import com.roxiemobile.networkingapi.network.rest.response.error.nested.ConversionException;
import com.roxiemobile.networkingapi.util.ResponseEntityUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

public abstract class AbstractValidatableModelConverter<T extends ValidatableModel>
        extends AbstractCallResultConverter<T> {

// MARK: - Construction

    protected AbstractValidatableModelConverter(@NotNull Class<T> classOfType) {
        mClassOfType = classOfType;
    }

// MARK: - Methods

    @Override
    public @NotNull ResponseEntity<T> convert(@NotNull ResponseEntity<byte[]> responseEntity) throws ConversionException {
        ResponseEntity<T> newEntity;
        T newBody = null;

        try {
            @Nullable byte[] responseBody = responseEntity.body();

            // Try to convert HTTP response to POJO
            if (ArrayUtils.isNotEmpty(responseBody)) {
                ByteArrayInputStream stream = new ByteArrayInputStream(responseBody);
                newBody = DataMapper.fromJson(new InputStreamReader(stream), mClassOfType);
            }
        }
        catch (JsonSyntaxException | JsonIOException ex) {
            Logger.e(TAG, ex);
            throw new ConversionException(responseEntity, ex);
        }

        // Create new response entity
        newEntity = ResponseEntityUtils.copyWith(responseEntity, newBody);
        return newEntity;
    }

// MARK: - Constants

    public static final @NotNull String TAG = AbstractValidatableModelConverter.class.getSimpleName();

// MARK: - Variables

    private final @NotNull Class<T> mClassOfType;
}
