package com.roxiemobile.networkingapi.network.rest;

import com.roxiemobile.networkingapi.network.rest.request.RequestEntity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Call<T> {

    /**
     * TODO
     */
    @Nullable String tag();

    /**
     * The original request entity.
     */
    @NotNull RequestEntity<T> requestEntity();
}
