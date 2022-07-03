package com.roxiemobile.networkingapi.network.rest.request;

import com.roxiemobile.androidcommons.diagnostics.Guard;
import com.roxiemobile.networkingapi.network.http.CookieStore;
import com.roxiemobile.networkingapi.network.http.HttpHeaders;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;

public class BasicRequestEntity<T> implements RequestEntity<T> {

// MARK: - Construction

    protected BasicRequestEntity(@NotNull Builder<T> builder) {
        // Init instance variables
        mUri = builder.mUri;
        mHeaders = builder.mHeaders;
        mCookieStore = builder.mCookieStore;
        mBody = builder.mBody;
    }

// MARK: - Properties

    @Override
    public @NotNull URI uri() {
        return mUri;
    }

    @Override
    public @Nullable HttpHeaders headers() {
        return mHeaders;
    }

    @Override
    public @Nullable CookieStore cookieStore() {
        return mCookieStore;
    }

    @Override
    public @Nullable T body() {
        return mBody;
    }

// MARK: - Inner Types

    public static class Builder<T> {

        public Builder() {
            // Do nothing
        }

        public Builder(@NotNull RequestEntity<T> entity) {
            Guard.notNull(entity, "entity is null");

            // Init instance variables
            mUri = entity.uri();
            mHeaders = entity.headers();
            mCookieStore = entity.cookieStore();
            mBody = entity.body();
        }

        public <Ti> Builder(@NotNull RequestEntity<Ti> entity, @Nullable T body) {
            Guard.notNull(entity, "entity is null");

            // Init instance variables
            mUri = entity.uri();
            mHeaders = entity.headers();
            mCookieStore = entity.cookieStore();
            mBody = body;
        }

        public @NotNull Builder<T> uri(@NotNull URI uri) {
            mUri = uri;
            return this;
        }

        public @NotNull Builder<T> headers(@Nullable HttpHeaders headers) {
            mHeaders = headers;
            return this;
        }

        public @NotNull Builder<T> cookieStore(@Nullable CookieStore cookieStore) {
            mCookieStore = cookieStore;
            return this;
        }

        public @NotNull Builder<T> body(@Nullable T body) {
            mBody = body;
            return this;
        }

        public @NotNull RequestEntity<T> build() {
            Guard.notNull(uri(), "url is null");
            return new BasicRequestEntity<>(this);
        }

        protected @Nullable URI uri() {
            return mUri;
        }

        private @Nullable URI mUri;
        private @Nullable HttpHeaders mHeaders;
        private @Nullable CookieStore mCookieStore;
        private @Nullable T mBody;
    }

// MARK: - Variables

    private final @NotNull URI mUri;

    private final @Nullable HttpHeaders mHeaders;

    private final @Nullable CookieStore mCookieStore;

    private final @Nullable T mBody;
}
