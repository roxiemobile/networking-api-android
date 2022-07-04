package com.roxiemobile.networkingapi.network.rest.response.error.nested;

import com.annimon.stream.function.Consumer;
import com.roxiemobile.androidcommons.diagnostics.Guard;
import com.roxiemobile.androidcommons.logging.Logger;
import com.roxiemobile.androidcommons.util.StringUtils;
import com.roxiemobile.networkingapi.network.rest.CallResultConverter;
import com.roxiemobile.networkingapi.network.rest.converter.StringConverter;
import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

abstract class AbstractNestedError extends Exception
        implements ResponseEntityHolder {

// MARK: - Construction

    /**
     * Construct a new instance of {@code NestedRestApiErrorImpl} based on a {@link ResponseEntity}.
     */
    public AbstractNestedError(@NotNull ResponseEntity<byte[]> responseEntity) {
        this(responseEntity, null);
    }

    /**
     * Construct a new instance of {@code NestedRestApiErrorImpl} based on a {@link ResponseEntity} and cause.
     */
    public AbstractNestedError(@NotNull ResponseEntity<byte[]> responseEntity, @Nullable Throwable cause) {
        super(cause);
        Guard.notNull(responseEntity, "responseEntity is null");

        mResponseEntity = responseEntity;
    }

// MARK: - Properties

    /**
     * Returns the HTTP response entity.
     */
    @Override
    public @NotNull ResponseEntity<byte[]> getResponseEntity() {
        return mResponseEntity;
    }

    /**
     * Returns the response body as a byte array.
     */
    @Override
    public @Nullable byte[] responseBodyAsBytes() {
        return mResponseEntity.getBody();
    }

    /**
     * Returns the response body as a string.
     */
    @Override
    public @Nullable String responseBodyAsString() {
        try {
            return CONVERTER.convert(mResponseEntity).getBody();
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
    public void printErrorDescription(@NotNull Consumer<String> consumer) {
        String message = responseBodyAsString();

        if (StringUtils.isNotEmpty(message)) {
            consumer.accept(message);
        }
    }

// MARK: - Constants

    private static final @NotNull String TAG = AbstractNestedError.class.getSimpleName();

    private static final @NotNull CallResultConverter<byte[], String> CONVERTER =
            new StringConverter();

// MARK: - Variables

    private final @NotNull ResponseEntity<byte[]> mResponseEntity;
}
