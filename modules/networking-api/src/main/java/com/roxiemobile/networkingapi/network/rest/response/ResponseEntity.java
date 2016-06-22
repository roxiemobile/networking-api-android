package com.roxiemobile.networkingapi.network.rest.response;

import com.roxiemobile.networkingapi.network.http.HttpStatus;
import com.roxiemobile.networkingapi.network.http.MediaType;
import com.roxiemobile.networkingapi.network.rest.request.RequestEntity;

public interface ResponseEntity<T> extends RequestEntity<T>
{
    /**
     * TODO
     */
    HttpStatus status();

    /**
     * TODO
     */
    MediaType mediaType();
}
