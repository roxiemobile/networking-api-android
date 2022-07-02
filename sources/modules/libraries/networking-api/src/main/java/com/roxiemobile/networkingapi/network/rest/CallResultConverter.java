package com.roxiemobile.networkingapi.network.rest;

import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity;
import com.roxiemobile.networkingapi.network.rest.response.error.nested.ConversionException;

public interface CallResultConverter<Ti, To> {

    /**
     * Converts result from one format to another.
     */
    CallResult<To> convert(CallResult<Ti> result);

    /**
     * Converts response entity from one format to another.
     */
    ResponseEntity<To> convert(ResponseEntity<Ti> entity) throws ConversionException;
}
