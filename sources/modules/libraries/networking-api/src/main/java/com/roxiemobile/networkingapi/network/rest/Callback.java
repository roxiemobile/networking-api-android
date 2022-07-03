package com.roxiemobile.networkingapi.network.rest;

import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity;
import com.roxiemobile.networkingapi.network.rest.response.RestApiError;

import org.jetbrains.annotations.NotNull;

public interface Callback<Ti, To> {

    /**
     * TODO
     */
    boolean onShouldExecute(@NotNull Call<Ti> call);

    /**
     * TODO
     */
    void onSuccess(@NotNull Call<Ti> call, @NotNull ResponseEntity<To> entity);

    /**
     * TODO
     */
    void onFailure(@NotNull Call<Ti> call, @NotNull RestApiError error);

    /**
     * TODO
     */
    void onCancel(@NotNull Call<Ti> call);
}
