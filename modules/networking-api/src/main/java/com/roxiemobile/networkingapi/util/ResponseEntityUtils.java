package com.roxiemobile.networkingapi.util;

import android.support.annotation.NonNull;

import com.roxiemobile.networkingapi.network.rest.response.BasicResponseEntity;
import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity;

import static com.roxiemobile.androidcommons.util.AssertUtils.assertNotNull;

public final class ResponseEntityUtils
{
// MARK: - Construction

    private ResponseEntityUtils() {
        // Do nothing
    }

// MARK: - Methods

    @Deprecated
    public static <Ti, To> ResponseEntity<To> copy(@NonNull ResponseEntity<Ti> entity) {
        assertNotNull(entity, "entity == null");
        //noinspection unchecked
        return copyWith(entity, (To) entity.body());
    }

    @Deprecated
    public static <Ti, To> ResponseEntity<To> copyWith(ResponseEntity<Ti> entity, To body) {
        return new BasicResponseEntity.Builder<>(entity, body).build();
    }

}
