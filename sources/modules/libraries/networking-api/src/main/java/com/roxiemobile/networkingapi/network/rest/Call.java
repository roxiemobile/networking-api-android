package com.roxiemobile.networkingapi.network.rest;

import com.roxiemobile.networkingapi.network.rest.request.RequestEntity;

public interface Call<T>
{
    /**
     * TODO
     */
    String tag();

    /**
     * The original request entity.
     */
    RequestEntity<T> requestEntity();
}
