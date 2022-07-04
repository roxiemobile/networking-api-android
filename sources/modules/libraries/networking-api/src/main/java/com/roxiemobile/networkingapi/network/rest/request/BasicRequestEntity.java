package com.roxiemobile.networkingapi.network.rest.request;

import com.roxiemobile.androidcommons.diagnostics.Guard;
import com.roxiemobile.networkingapi.network.http.CookieStore;
import com.roxiemobile.networkingapi.network.http.HttpHeaders;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.Objects;

public class BasicRequestEntity<T> implements RequestEntity<T> {

// MARK: - Construction

    protected BasicRequestEntity(@NotNull Builder<T> builder) {
        _link = Objects.requireNonNull(builder.link, "link is null");
        _httpHeaders = builder.httpHeaders;
        _cookieStore = builder.cookieStore;
        _body = builder.body;
    }

// MARK: - Properties

    @Override
    public @NotNull URI getLink() {
        return _link;
    }

    @Override
    public @Nullable HttpHeaders getHttpHeaders() {
        return _httpHeaders;
    }

    @Override
    public @Nullable CookieStore getCookieStore() {
        return _cookieStore;
    }

    @Override
    public @Nullable T getBody() {
        return _body;
    }

// MARK: - Inner Types

    public static class Builder<T> {

        public Builder() {
            // Do nothing
        }

        public Builder(@NotNull RequestEntity<T> requestEntity) {
            Guard.notNull(requestEntity, "requestEntity is null");

            this.link = requestEntity.getLink();
            this.httpHeaders = requestEntity.getHttpHeaders();
            this.cookieStore = requestEntity.getCookieStore();
            this.body = requestEntity.getBody();
        }

        public <Ti> Builder(@NotNull RequestEntity<Ti> requestEntity, @Nullable T body) {
            Guard.notNull(requestEntity, "requestEntity is null");

            this.link = requestEntity.getLink();
            this.httpHeaders = requestEntity.getHttpHeaders();
            this.cookieStore = requestEntity.getCookieStore();
            this.body = body;
        }

        public @NotNull Builder<T> link(@NotNull URI link) {
            this.link = link;
            return this;
        }

        public @NotNull Builder<T> httpHeaders(@Nullable HttpHeaders httpHeaders) {
            this.httpHeaders = httpHeaders;
            return this;
        }

        public @NotNull Builder<T> cookieStore(@Nullable CookieStore cookieStore) {
            this.cookieStore = cookieStore;
            return this;
        }

        public @NotNull Builder<T> body(@Nullable T body) {
            this.body = body;
            return this;
        }

        public @NotNull RequestEntity<T> build() {
            return new BasicRequestEntity<>(this);
        }

        private @Nullable URI link;
        private @Nullable HttpHeaders httpHeaders;
        private @Nullable CookieStore cookieStore;
        private @Nullable T body;
    }

// MARK: - Variables

    private final @NotNull URI _link;

    private final @Nullable HttpHeaders _httpHeaders;

    private final @Nullable CookieStore _cookieStore;

    private final @Nullable T _body;
}
