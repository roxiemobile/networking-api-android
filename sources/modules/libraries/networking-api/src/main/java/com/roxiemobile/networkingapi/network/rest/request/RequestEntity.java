package com.roxiemobile.networkingapi.network.rest.request;

import com.roxiemobile.networkingapi.network.http.CookieStore;
import com.roxiemobile.networkingapi.network.http.HttpHeaders;

import org.jetbrains.annotations.Nullable;

import java.net.URI;

public interface RequestEntity<T> {

    /**
     * TODO
     */
    URI uri();

    /**
     * TODO
     */
    HttpHeaders headers();

    /**
     * TODO
     */
    CookieStore cookieStore();

    /**
     * TODO
     */
    @Nullable T body();
}
