package com.roxiemobile.networkingapi.network.rest.request;

import com.roxiemobile.networkingapi.network.rest.Task;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface TaskBuilder<Ti, To> {

    /**
     * TODO
     */
    @Nullable String tag();

    /**
     * The original HTTP request.
     */
    @Nullable RequestEntity<Ti> requestEntity();

    /**
     * TODO
     */
    @NotNull Task<Ti, To> build();
}
