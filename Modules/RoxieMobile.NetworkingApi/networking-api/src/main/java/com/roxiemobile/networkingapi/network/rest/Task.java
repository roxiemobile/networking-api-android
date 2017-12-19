package com.roxiemobile.networkingapi.network.rest;

public interface Task<Ti, To> extends Call<Ti>, Cloneable
{
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
    Task<Ti, To> clone();
}
