package com.roxiemobile.networkingapi.network.rest.request;

import com.roxiemobile.networkingapi.network.http.CookieStore;
import com.roxiemobile.networkingapi.network.http.HttpHeaders;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;

public interface RequestEntity<T> {

// MARK: - Properties

    /**
     * TODO
     */
    @NotNull URI getLink();

    /**
     * TODO
     */
    @Nullable HttpHeaders getHttpHeaders();

    /**
     * TODO
     */
    @Nullable CookieStore getCookieStore();

    /**
     * TODO
     */
    @Nullable T getBody();
}
