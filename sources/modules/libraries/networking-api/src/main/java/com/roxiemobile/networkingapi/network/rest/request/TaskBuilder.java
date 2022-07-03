package com.roxiemobile.networkingapi.network.rest.request;

import com.roxiemobile.networkingapi.network.rest.Task;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface TaskBuilder<Ti, To> {

// MARK: - Properties

    /**
     * TODO
     */
    @Nullable String getTag();

    /**
     * The original HTTP request.
     */
    @Nullable RequestEntity<Ti> getRequestEntity();

// MARK: - Methods

    /**
     * TODO
     */
    @NotNull Task<Ti, To> build();
}
