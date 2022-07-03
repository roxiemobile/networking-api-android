package com.roxiemobile.networkingapi.util;

import com.roxiemobile.networkingapi.network.rest.request.BasicRequestEntity;
import com.roxiemobile.networkingapi.network.rest.request.RequestEntity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class RequestEntityUtils {

// MARK: - Construction

    private RequestEntityUtils() {
        // Do nothing
    }

// MARK: - Methods

    public static @NotNull <Ti, To> RequestEntity<To> copyWith(@NotNull RequestEntity<Ti> entity, @Nullable To body) {
        return new BasicRequestEntity.Builder<>(entity, body).build();
    }
}
