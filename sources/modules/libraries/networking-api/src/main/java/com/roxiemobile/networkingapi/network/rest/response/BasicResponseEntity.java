package com.roxiemobile.networkingapi.network.rest.response;

import com.roxiemobile.androidcommons.diagnostics.Guard;
import com.roxiemobile.networkingapi.network.http.CookieStore;
import com.roxiemobile.networkingapi.network.http.HttpHeaders;
import com.roxiemobile.networkingapi.network.http.HttpStatus;
import com.roxiemobile.networkingapi.network.http.MediaType;
import com.roxiemobile.networkingapi.network.rest.request.BasicRequestEntity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;

public class BasicResponseEntity<T> extends BasicRequestEntity<T> implements ResponseEntity<T> {

// MARK: - Construction

    private BasicResponseEntity(@NotNull Builder<T> builder) {
        super(builder);

        // Init instance variables
        mHttpStatus = builder.status();
        mMediaType = builder.mediaType();
    }

// MARK: - Properties

    @Override
    public @NotNull HttpStatus status() {
        return mHttpStatus;
    }

    @Override
    public @NotNull MediaType mediaType() {
        return mMediaType;
    }

// MARK: - Inner Types

    public static class Builder<T> extends BasicRequestEntity.Builder<T> {

        public Builder() {
            // Do nothing
        }

        public Builder(@NotNull ResponseEntity<T> responseEntity) {
            super(responseEntity);

            // Init instance variables
            mHttpStatus = responseEntity.status();
            mMediaType = responseEntity.mediaType();
        }

        public <Ti> Builder(@NotNull ResponseEntity<Ti> responseEntity, @Nullable T body) {
            super(responseEntity, body);

            // Init instance variables
            mHttpStatus = responseEntity.status();
            mMediaType = responseEntity.mediaType();
        }

        @Override
        public @NotNull Builder<T> uri(@NotNull URI link) {
            return (Builder<T>) super.uri(link);
        }

        @Override
        public @NotNull Builder<T> headers(@Nullable HttpHeaders httpHeaders) {
            return (Builder<T>) super.headers(httpHeaders);
        }

        @Override
        public @NotNull Builder<T> cookieStore(@Nullable CookieStore cookieStore) {
            return (Builder<T>) super.cookieStore(cookieStore);
        }

        @Override
        public @NotNull Builder<T> body(@Nullable T body) {
            return (Builder<T>) super.body(body);
        }

        public @NotNull Builder<T> status(@NotNull HttpStatus httpStatus) {
            mHttpStatus = httpStatus;
            return this;
        }

        public @NotNull Builder<T> mediaType(@NotNull MediaType mediaType) {
            mMediaType = mediaType;
            return this;
        }

        @Override
        public @NotNull ResponseEntity<T> build() {
            Guard.notNull(uri(), "url is null");
            Guard.notNull(status(), "httpStatus is null");
            Guard.notNull(mediaType(), "mediaType is null");
            return new BasicResponseEntity<>(this);
        }

        protected @Nullable HttpStatus status() {
            return mHttpStatus;
        }

        protected @Nullable MediaType mediaType() {
            return mMediaType;
        }

        private @Nullable HttpStatus mHttpStatus;
        private @Nullable MediaType mMediaType;
    }

// MARK: - Variables

    private final @NotNull HttpStatus mHttpStatus;

    private final @NotNull MediaType mMediaType;
}
