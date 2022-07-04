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

public abstract class AbstractCallResultConverter<T> implements CallResultConverter<byte[], T> {

// MARK: - Methods

    public @NotNull CallResult<T> convert(@NotNull CallResult<byte[]> callResult) {
        CallResult<T> newResult;

        // Handle call result
        if (callResult.isSuccess()) {
            try {
                ResponseEntity<byte[]> responseEntity = callResult.value();
                checkMediaType(responseEntity);

                // Convert response entity
                ResponseEntity<T> newResponseEntity = convert(responseEntity);
                newResult = CallResult.success(newResponseEntity);
            }
            catch (UnexpectedMediaTypeException | ConversionException ex) {

                // Build new error with caught exception
                RestApiError error = new ApplicationLayerError(ex);
                newResult = CallResult.failure(error);
            }
        }
        else {
            // Copy an original error
            newResult = CallResult.failure(callResult.error());
        }

        // Done
        return newResult;
    }

    protected abstract @NotNull MediaType[] supportedMediaTypes();

// MARK: - Private Methods

    private void checkMediaType(@NotNull ResponseEntity<byte[]> responseEntity) throws UnexpectedMediaTypeException {
        Guard.notNull(responseEntity, "responseEntity is null");

        MediaType mediaType = responseEntity.mediaType();
        boolean found = false;

        // Search for compatible MediaType
        if (mediaType != null) {
            for (MediaType type : supportedMediaTypes()) {
                if (found = mediaType.isCompatibleWith(type)) {
                    break;
                }
            }
        }

        // Throw exception if on MediaType found
        if (!found) {
            throw new UnexpectedMediaTypeException(responseEntity);
        }
    }
}
