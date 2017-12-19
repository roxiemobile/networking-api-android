package com.roxiemobile.networkingapi.network.rest.response;

import android.support.annotation.NonNull;

import com.roxiemobile.androidcommons.diagnostics.Guard;
import com.roxiemobile.networkingapi.network.http.CookieStore;
import com.roxiemobile.networkingapi.network.http.HttpHeaders;
import com.roxiemobile.networkingapi.network.http.HttpStatus;
import com.roxiemobile.networkingapi.network.http.MediaType;
import com.roxiemobile.networkingapi.network.rest.request.BasicRequestEntity;

import java.net.URI;

public class BasicResponseEntity<T> extends BasicRequestEntity<T> implements ResponseEntity<T>
{
// MARK: - Construction

    private BasicResponseEntity(@NonNull Builder<T> builder) {
        super(builder);

        // Init instance variables
        mStatus = builder.status();
        mMediaType = builder.mediaType();
    }

// MARK: - Properties

    @Override
    public @NonNull HttpStatus status() {
        return mStatus;
    }

    @Override
    public @NonNull MediaType mediaType() {
        return mMediaType;
    }

// MARK: - Inner Types

    public static class Builder<T> extends BasicRequestEntity.Builder<T>
    {
        public Builder() {
            // Do nothing
        }

        public Builder(@NonNull ResponseEntity<T> entity) {
            super(entity);

            // Init instance variables
            mStatus = entity.status();
            mMediaType = entity.mediaType();
        }

        public <Ti> Builder(@NonNull ResponseEntity<Ti> entity, T body) {
            super(entity, body);

            // Init instance variables
            mStatus = entity.status();
            mMediaType = entity.mediaType();
        }

        @Override
        public Builder<T> uri(@NonNull URI uri) {
            return (Builder<T>) super.uri(uri);
        }

        @Override
        public Builder<T> headers(HttpHeaders headers) {
            return (Builder<T>) super.headers(headers);
        }

        @Override
        public Builder<T> cookieStore(CookieStore cookieStore) {
            return (Builder<T>) super.cookieStore(cookieStore);
        }

        @Override
        public Builder<T> body(T body) {
            return (Builder<T>) super.body(body);
        }

        public Builder<T> status(HttpStatus status) {
            mStatus = status;
            return this;
        }

        public Builder<T> mediaType(MediaType mediaType) {
            mMediaType = mediaType;
            return this;
        }

        @Override
        public ResponseEntity<T> build() {
            Guard.notNull(uri(), "url is null");
            Guard.notNull(status(), "status is null");
            Guard.notNull(mediaType(), "mediaType is null");
            return new BasicResponseEntity<>(this);
        }

        protected HttpStatus status() {
            return mStatus;
        }

        protected MediaType mediaType() {
            return mMediaType;
        }

        private HttpStatus mStatus;
        private MediaType mMediaType;
    }

// MARK: - Variables

    private final @NonNull HttpStatus mStatus;

    private final @NonNull MediaType mMediaType;
}
