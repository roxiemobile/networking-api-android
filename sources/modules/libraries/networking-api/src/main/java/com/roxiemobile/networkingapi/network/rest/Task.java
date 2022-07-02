package com.roxiemobile.networkingapi.network.rest;

import org.jetbrains.annotations.NotNull;

public interface Task<Ti, To> extends Call<Ti>, Cloneable {

    /**
     * TODO
     */
    void execute(Callback<Ti, To> callback);

    /**
     * TODO
     */
    Cancellable enqueue(Callback<Ti, To> callback, boolean callbackOnUiThread);

    /**
     * TODO
     */
    @NotNull Task<Ti, To> clone();
}
