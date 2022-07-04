package com.roxiemobile.networkingapi.network.rest;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Task<Ti, To> extends Call<Ti>, Cloneable {

    /**
     * TODO
     */
    void execute(@Nullable Callback<Ti, To> callback);

    /**
     * TODO
     */
    @NotNull Cancellable enqueue(@Nullable Callback<Ti, To> callback, boolean callbackOnUiThread);

    /**
     * TODO
     */
    @NotNull Task<Ti, To> clone();
}
