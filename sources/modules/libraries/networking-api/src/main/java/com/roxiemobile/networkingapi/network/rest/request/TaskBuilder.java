package com.roxiemobile.networkingapi.network.rest.request;

import com.roxiemobile.networkingapi.network.rest.Task;

public interface TaskBuilder<Ti, To> {

    /**
     * TODO
     */
    String tag();

    /**
     * The original HTTP request.
     */
    RequestEntity<Ti> requestEntity();

    /**
     * TODO
     */
    Task<Ti, To> build();
}
