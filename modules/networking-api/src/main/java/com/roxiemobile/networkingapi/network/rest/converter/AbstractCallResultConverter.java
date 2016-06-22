package com.roxiemobile.networkingapi.network.rest.converter;

import android.support.annotation.NonNull;

import com.roxiemobile.networkingapi.network.http.MediaType;
import com.roxiemobile.networkingapi.network.rest.CallResult;
import com.roxiemobile.networkingapi.network.rest.CallResultConverter;
import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity;
import com.roxiemobile.networkingapi.network.rest.response.RestApiError;
import com.roxiemobile.networkingapi.network.rest.response.error.ApplicationLayerError;
import com.roxiemobile.networkingapi.network.rest.response.error.nested.ConversionException;
import com.roxiemobile.networkingapi.network.rest.response.error.nested.UnexpectedMediaTypeException;

import static com.roxiemobile.androidcommons.util.AssertUtils.assertNotNull;

abstract class AbstractCallResultConverter<T> implements CallResultConverter<byte[], T>
{
// MARK: - Methods

    public @NonNull CallResult<T> convert(@NonNull CallResult<byte[]> result) {
        CallResult<T> newResult;

        // Handle call result
        if (result.isSuccess()) {
            try {
                ResponseEntity<byte[]> entity = result.value();
                checkMediaType(entity);

                // Convert response entity
                ResponseEntity<T> response = convert(entity);
                newResult = CallResult.success(response);
            }
            catch (UnexpectedMediaTypeException | ConversionException ex) {

                // Build new error with caught exception
                RestApiError error = new ApplicationLayerError(ex);
                newResult = CallResult.failure(error);
            }
        }
        else {
            // Copy an original error
            newResult = CallResult.failure(result.error());
        }

        // Done
        return newResult;
    }

    public abstract @NonNull ResponseEntity<T> convert(@NonNull ResponseEntity<byte[]> entity)
            throws ConversionException;

    protected abstract @NonNull MediaType[] supportedMediaTypes();

// MARK: - Private Methods

    private void checkMediaType(@NonNull ResponseEntity<byte[]> entity) throws UnexpectedMediaTypeException {
        assertNotNull(entity, "entity == null");

        MediaType mediaType = entity.mediaType();
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
            throw new UnexpectedMediaTypeException(entity);
        }
    }

}
