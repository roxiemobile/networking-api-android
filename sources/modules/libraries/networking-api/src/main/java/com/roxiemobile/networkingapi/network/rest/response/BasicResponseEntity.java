package com.roxiemobile.networkingapi.network.rest.response;

import com.roxiemobile.networkingapi.network.http.CookieStore;
import com.roxiemobile.networkingapi.network.http.HttpHeaders;
import com.roxiemobile.networkingapi.network.http.HttpStatus;
import com.roxiemobile.networkingapi.network.http.MediaType;
import com.roxiemobile.networkingapi.network.rest.request.BasicRequestEntity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.Objects;

public class BasicResponseEntity<T> extends BasicRequestEntity<T> implements ResponseEntity<T> {

// MARK: - Construction

    private BasicResponseEntity(@NotNull Builder<T> builder) {
        super(builder);

        _httpStatus = Objects.requireNonNull(builder.httpStatus, "httpStatus is null");
        _mediaType = Objects.requireNonNull(builder.mediaType, "mediaType is null");
    }

// MARK: - Properties

    @Override
    public @NotNull HttpStatus getHttpStatus() {
        return _httpStatus;
    }

    @Override
    public @NotNull MediaType getMediaType() {
        return _mediaType;
    }

// MARK: - Inner Types

    public static class Builder<T> extends BasicRequestEntity.Builder<T> {

        public Builder() {
            // Do nothing
        }

        public Builder(@NotNull ResponseEntity<T> responseEntity) {
            super(responseEntity);

            this.httpStatus = responseEntity.getHttpStatus();
            this.mediaType = responseEntity.getMediaType();
        }

        public <Ti> Builder(@NotNull ResponseEntity<Ti> responseEntity, @Nullable T body) {
            super(responseEntity, body);

            this.httpStatus = responseEntity.getHttpStatus();
            this.mediaType = responseEntity.getMediaType();
        }

        @Override
        public @NotNull Builder<T> link(@NotNull URI link) {
            return (Builder<T>) super.link(link);
        }

        @Override
        public @NotNull Builder<T> httpHeaders(@Nullable HttpHeaders httpHeaders) {
            return (Builder<T>) super.httpHeaders(httpHeaders);
        }

        @Override
        public @NotNull Builder<T> cookieStore(@Nullable CookieStore cookieStore) {
            return (Builder<T>) super.cookieStore(cookieStore);
        }

        @Override
        public @NotNull Builder<T> body(@Nullable T body) {
            return (Builder<T>) super.body(body);
        }

        public @NotNull Builder<T> httpStatus(@NotNull HttpStatus httpStatus) {
            this.httpStatus = httpStatus;
            return this;
        }

        public @NotNull Builder<T> mediaType(@NotNull MediaType mediaType) {
            this.mediaType = mediaType;
            return this;
        }

        @Override
        public @NotNull ResponseEntity<T> build() {
            return new BasicResponseEntity<>(this);
        }

        private @Nullable HttpStatus httpStatus;
        private @Nullable MediaType mediaType;
    }

// MARK: - Variables

    private final @NotNull HttpStatus _httpStatus;

    private final @NotNull MediaType _mediaType;
}
