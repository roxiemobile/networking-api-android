package com.roxiemobile.networkingapi.network.rest.converter;

import com.roxiemobile.androidcommons.diagnostics.Guard;
import com.roxiemobile.networkingapi.network.http.MediaType;
import com.roxiemobile.networkingapi.network.rest.CallResult;
import com.roxiemobile.networkingapi.network.rest.CallResultConverter;
import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity;
import com.roxiemobile.networkingapi.network.rest.response.RestApiError;
import com.roxiemobile.networkingapi.network.rest.response.error.ApplicationLayerError;
import com.roxiemobile.networkingapi.network.rest.response.error.nested.ConversionException;
import com.roxiemobile.networkingapi.network.rest.response.error.nested.UnexpectedMediaTypeException;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class AbstractCallResultConverter<T> implements CallResultConverter<byte[], T> {

// MARK: - Methods

    public @NotNull CallResult<T> convert(@NotNull CallResult<byte[]> callResult) {
        @NotNull CallResult<T> newCallResult;

        // Handle call result
        if (callResult.isSuccess()) {
            try {
                @NotNull ResponseEntity<byte[]> responseEntity = Objects.requireNonNull(callResult.value(), "responseEntity is null");
                checkMediaType(responseEntity);

                // Convert response entity
                @NotNull ResponseEntity<T> newResponseEntity = convert(responseEntity);
                newCallResult = CallResult.success(newResponseEntity);
            }
            catch (UnexpectedMediaTypeException | ConversionException ex) {

                // Build new error with caught exception
                @NotNull RestApiError error = new ApplicationLayerError(ex);
                newCallResult = CallResult.failure(error);
            }
        }
        else {
            // Copy an original error
            @NotNull RestApiError error = Objects.requireNonNull(callResult.error(), "error is null");
            newCallResult = CallResult.failure(error);
        }

        // Done
        return newCallResult;
    }

    protected abstract @NotNull MediaType[] supportedMediaTypes();

// MARK: - Private Methods

    private void checkMediaType(@NotNull ResponseEntity<byte[]> responseEntity) throws UnexpectedMediaTypeException {
        Guard.notNull(responseEntity, "responseEntity is null");

        @NotNull MediaType mediaType = responseEntity.getMediaType();
        boolean found = false;

        // Search for compatible MediaType
        for (MediaType type : supportedMediaTypes()) {
            if (mediaType.isCompatibleWith(type)) {
                found = true;
                break;
            }
        }

        // Throw exception if on MediaType found
        if (!found) {
            throw new UnexpectedMediaTypeException(responseEntity);
        }
    }
}
