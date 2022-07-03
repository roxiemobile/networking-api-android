package com.roxiemobile.networkingapi.util;

import com.roxiemobile.androidcommons.logging.Logger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.URISyntaxException;

public final class URIUtils {

// MARK: - Construction

    private URIUtils() {
        // Do nothing
    }

// MARK: - Methods

    /**
     * TODO
     */
    public static @Nullable URI tryParseURI(@Nullable String spec) {
        URI result = null;
        try {
            if (spec != null) {
                result = new URI(spec);
            }
        }
        catch (URISyntaxException ex) {
            Logger.w(TAG, ex);
        }
        return result;
    }

    /**
     * Returns the port to use for {@code scheme} connections.
     */
    public static int getEffectivePort(@Nullable String scheme, int specifiedPort) {
        if (specifiedPort != -1) {
            return specifiedPort;
        }
        else if ("http".equalsIgnoreCase(scheme)) {
            return 80;
        }
        else if ("https".equalsIgnoreCase(scheme)) {
            return 443;
        }
        else {
            return -1;
        }
    }

    public static int getEffectivePort(@NotNull URI uri) {
        return getEffectivePort(uri.getScheme(), uri.getPort());
    }

// MARK: - Constants

    private static final @NotNull String TAG = URIUtils.class.getSimpleName();
}
