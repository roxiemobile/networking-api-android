package com.roxiemobile.networkingapi.network.rest.response.error;

import android.support.annotation.NonNull;

import com.roxiemobile.androidcommons.data.model.SerializableObject;
import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity;

import static com.roxiemobile.androidcommons.util.AssertUtils.assertNotNull;

@Deprecated
public class TopLevelProtocolError extends RestApiErrorImpl
{
// MARK: - Construction

    public TopLevelProtocolError(@NonNull ResponseEntity<SerializableObject> entity) {
        super(null);
        assertNotNull(entity, "entity == null");

        // Init instance variables
        mResponseEntity = entity;
    }

// MARK: - Properties

    /**
     * Returns the type of an error.
     */
    @Override
    public RestApiErrorType getType() {
        return RestApiErrorType.kTopLevelProtocol;
    }

    /**
     * Returns the HTTP response entity.
     */
    public @NonNull ResponseEntity<SerializableObject> getResponseEntity() {
        return mResponseEntity;
    }

// MARK: - Variables

    private final ResponseEntity<SerializableObject> mResponseEntity;

}
