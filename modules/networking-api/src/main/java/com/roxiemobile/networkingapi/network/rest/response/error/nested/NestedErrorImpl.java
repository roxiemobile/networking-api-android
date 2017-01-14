package com.roxiemobile.networkingapi.network.rest.response.error.nested;

import android.support.annotation.NonNull;

import com.annimon.stream.function.Consumer;
import com.roxiemobile.androidcommons.logging.Logger;
import com.roxiemobile.androidcommons.util.StringUtils;
import com.roxiemobile.networkingapi.network.rest.CallResultConverter;
import com.roxiemobile.networkingapi.network.rest.converter.StringConverter;
import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity;

import static com.roxiemobile.androidcommons.diagnostics.Require.requireNotNull;

abstract class NestedErrorImpl extends Exception
        implements ResponseEntityHolder
{
// MARK: - Construction

    /**
     * Construct a new instance of {@code NestedRestApiErrorImpl} based on a {@link ResponseEntity}.
     */
    public NestedErrorImpl(@NonNull ResponseEntity<byte[]> entity) {
        this(entity, null);
    }

    /**
     * Construct a new instance of {@code NestedRestApiErrorImpl} based on a {@link ResponseEntity} and cause.
     */
    public NestedErrorImpl(@NonNull ResponseEntity<byte[]> entity, Throwable cause) {
        super(cause);
        requireNotNull(entity, "entity is null");

        // Init instance variables
        mResponseEntity = entity;
    }

// MARK: - Properties

    /**
     * Returns the HTTP response entity.
     */
    @Override
    public ResponseEntity<byte[]> getResponseEntity() {
        return mResponseEntity;
    }

    /**
     * Returns the response body as a byte array.
     */
    @Override
    public byte[] getResponseBodyAsBytes() {
        return mResponseEntity.body();
    }

    /**
     * Returns the response body as a string.
     */
    @Override
    public String getResponseBodyAsString() {
        try {
            return CONVERTER.convert(mResponseEntity).body();
        }
        catch (ConversionException ex) {
            Logger.e(TAG, ex);

            // Should not occur
            throw new InternalError(ex.getMessage());
        }
    }

    /**
     * Sends a printable representation of this {@code RestApiError}'s description
     * to the consumer.
     */
    @Override
    public void printErrorDescription(@NonNull Consumer<String> consumer) {
        String message = getResponseBodyAsString();

        if (!StringUtils.isNullOrEmpty(message)) {
            consumer.accept(message);
        }
    }

// MARK: - Constants

    private static final String TAG = NestedErrorImpl.class.getSimpleName();

    private static final CallResultConverter<byte[], String> CONVERTER =
            new StringConverter();

// MARK: - Variables

    private final ResponseEntity<byte[]> mResponseEntity;

}
