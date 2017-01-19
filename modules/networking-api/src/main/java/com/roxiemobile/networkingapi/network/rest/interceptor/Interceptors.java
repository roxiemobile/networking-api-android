package com.roxiemobile.networkingapi.network.rest.interceptor;

import okhttp3.Interceptor;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;

public final class Interceptors
{
// MARK: - Construction

    private Interceptors() {
        // Do nothing
    }

// MARK: - Methods

    public static Interceptor newHttpLoggingInterceptor(Level level) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();

        // Log request and response lines and their respective headers and bodies (if present).
        logging.setLevel(level);
        return logging;
    }

    public static Interceptor newHttpLoggingInterceptor() {
        return newHttpLoggingInterceptor(Level.BODY);
    }
}
