package com.roxiemobile.networkingapi.network.rest;

import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity;
import com.roxiemobile.networkingapi.network.rest.response.error.nested.ConversionException;

import org.jetbrains.annotations.NotNull;

public interface CallResultConverter<Ti, To> {

    /**
     * Converts result from one format to another.
     */
    @NotNull CallResult<To> convert(@NotNull CallResult<Ti> result);

    /**
     * Converts response entity from one format to another.
     */
    @NotNull ResponseEntity<To> convert(@NotNull ResponseEntity<Ti> entity) throws ConversionException;
}
