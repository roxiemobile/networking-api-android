package com.roxiemobile.networkingapi.network.rest.converter;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.roxiemobile.androidcommons.data.mapper.DataMapper;
import com.roxiemobile.androidcommons.data.model.ValidatableModel;
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
        _classOfType = classOfType;
    }

// MARK: - Methods

    @Override
    public @NotNull ResponseEntity<T> convert(@NotNull ResponseEntity<byte[]> responseEntity) throws ConversionException {

        @Nullable T newBody = null;
        try {

            @Nullable byte[] responseBody = responseEntity.getBody();

            // Try to convert HTTP response to POJO
            if (ArrayUtils.isNotEmpty(responseBody)) {
                @NotNull ByteArrayInputStream stream = new ByteArrayInputStream(responseBody);
                newBody = DataMapper.fromJson(new InputStreamReader(stream), _classOfType);
            }
        }
        catch (JsonSyntaxException | JsonIOException ex) {
            throw new ConversionException(responseEntity, ex);
        }

        // Create new response entity
        return ResponseEntityUtils.copyWith(responseEntity, newBody);
    }

// MARK: - Variables

    private final @NotNull Class<T> _classOfType;
}
