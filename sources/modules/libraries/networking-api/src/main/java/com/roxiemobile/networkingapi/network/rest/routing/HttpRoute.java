package com.roxiemobile.networkingapi.network.rest.routing;

import android.text.TextUtils;

import com.roxiemobile.androidcommons.data.Constants.Charsets;
import com.roxiemobile.androidcommons.diagnostics.Guard;
import com.roxiemobile.androidcommons.logging.Logger;
import com.roxiemobile.androidcommons.util.CollectionUtils;
import com.roxiemobile.networkingapi.network.http.util.LinkedMultiValueMap;
import com.roxiemobile.networkingapi.network.http.util.MultiValueMap;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public final class HttpRoute {

// MARK: - Construction

    private HttpRoute(@NotNull URI link) {
        Guard.notNull(link, "link is null");

        // Init instance variables
        mUri = link;
    }

// MARK: - Methods

    public static @NotNull HttpRoute buildRoute(@Nullable URI baseLink) {
        return buildRoute(baseLink, null);
    }

    public static @NotNull HttpRoute buildRoute(@Nullable URI baseLink, @Nullable String path) {
        return buildRoute(baseLink, path, null);
    }

    public static @NotNull HttpRoute buildRoute(@Nullable URI baseLink, @Nullable String path, @Nullable QueryParameters parameters) {
        @Nullable String linkText = null;

        // Build new URI
        if (baseLink != null) {
            linkText = baseLink.toString();

            // Append path to URI
            if (path != null) {
                linkText += path.trim();
            }

            // Append query parameters to URI
            if (parameters != null && parameters.size() > 0) {
                linkText += "?" + buildQueryString(parameters, Charsets.UTF_8);
            }
        }

        // Build new HTTP route
        @Nullable HttpRoute httpRoute = null;
        try {
            if (linkText != null) {
                httpRoute = new HttpRoute(new URI(linkText).normalize());
            }
        }
        catch (URISyntaxException ex) {
            Logger.e(TAG, ex);
        }

        // Validate result
        if (httpRoute == null) {
            throw new IllegalStateException("Could not create HTTP route for path ‘" + path + "’.");
        }

        // Done
        return httpRoute;
    }

    public @NotNull URI toURI() {
        return mUri;
    }

    public @NotNull String toString() {
        return mUri.toString();
    }

// MARK: - Private Methods

    @SuppressWarnings("SameParameterValue")
    private static @NotNull String buildQueryString(@NotNull QueryParameters parameters, @NotNull Charset charset) {
        @NotNull List<String> components = new LinkedList<>();

        try {
            // Build query string components
            for (String key : parameters.keySet()) {
                components.addAll(buildQueryStringComponents(key, parameters.get(key), charset));
            }
        }
        catch (UnsupportedEncodingException ex) {
            Logger.e(TAG, ex);

            // Re-throw internal error
            throw new IllegalStateException("Could not build query string.", ex);
        }

        // Done
        return TextUtils.join("&", components);
    }

    private static @NotNull List<String> buildQueryStringComponents(@NotNull String key, @NotNull List<String> values, @NotNull Charset charset)
            throws UnsupportedEncodingException {

        if (key == null || CollectionUtils.isEmpty(values) || charset == null) {
            throw new IllegalArgumentException();
        }

        List<String> components = new LinkedList<>();
        String charsetName = charset.name();
        String encodedValue;

        if (values.size() > 1) {
            for (String value : values) {
                encodedValue = URLEncoder.encode(key, charsetName) + "[]=" + URLEncoder.encode(value, charsetName);
                components.add(encodedValue);
            }
        }
        else {
            encodedValue = URLEncoder.encode(key, charsetName) + "=" + URLEncoder.encode(values.get(0), charsetName);
            components.add(encodedValue);
        }

        return components;
    }

// MARK: - Inner Types

    public static final class QueryParameters extends LinkedMultiValueMap<String, String> {

        public QueryParameters() {
            super();
        }

        public QueryParameters(int initialCapacity) {
            super(initialCapacity);
        }

        public QueryParameters(@Nullable MultiValueMap<String, String> otherParameters) {
            super(otherParameters == null ? Collections.emptyMap() : otherParameters);
        }
    }

// MARK: - Constants

    private static final @NotNull String TAG = HttpRoute.class.getSimpleName();

// MARK: - Variables

    private final @NotNull URI mUri;
}
