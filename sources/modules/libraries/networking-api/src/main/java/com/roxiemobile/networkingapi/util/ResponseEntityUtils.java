package com.roxiemobile.networkingapi.util;

import com.roxiemobile.androidcommons.diagnostics.Guard;
import com.roxiemobile.networkingapi.network.rest.response.BasicResponseEntity;
import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ResponseEntityUtils {

// MARK: - Construction

    private ResponseEntityUtils() {
        // Do nothing
    }

// MARK: - Methods

    @Deprecated
    public static @NotNull <Ti, To> ResponseEntity<To> copy(@NotNull ResponseEntity<Ti> entity) {
        Guard.notNull(entity, "entity is null");
        //noinspection unchecked
        return copyWith(entity, (To) entity.body());
    }

    @Deprecated
    public static @NotNull <Ti, To> ResponseEntity<To> copyWith(@NotNull ResponseEntity<Ti> entity, @Nullable To body) {
        return new BasicResponseEntity.Builder<>(entity, body).build();
    }
}
