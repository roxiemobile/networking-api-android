package com.roxiemobile.networkingapi.network.http;

import com.roxiemobile.androidcommons.diagnostics.Guard;

import org.jetbrains.annotations.NotNull;

/**
 * Represents HTTP Bearer Authentication.
 *
 * @see <a href="http://www.ietf.org/rfc/rfc6750.txt">RFC6750</a>
 */
public final class HttpBearerAuthentication extends HttpAuthentication {

// MARK: - Construction

    public HttpBearerAuthentication(@NotNull String token) {
        Guard.notEmpty(token, "token is empty");

        // Init instance variables
        mToken = token;
    }

// MARK: - Methods

    /**
     * Returns the value for the 'Authorization' HTTP header.
     */
    public @NotNull String getHeaderValue() {
        return "Bearer " + mToken;
    }

// MARK: - Variables

    private final @NotNull String mToken;
}
