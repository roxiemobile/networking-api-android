package com.roxiemobile.networkingapi.network.http;

import android.support.annotation.NonNull;

import com.roxiemobile.androidcommons.diagnostics.Guard;

/**
 * Represents HTTP Bearer Authentication.
 * @see <a href="http://www.ietf.org/rfc/rfc6750.txt">RFC6750</a>
 */
public class HttpBearerAuthentication extends HttpAuthentication
{
// MARK: - Construction

    public HttpBearerAuthentication(@NonNull String token) {
        Guard.notEmpty(token, "token is empty");

        // Init instance variables
        mToken = token;
    }

// MARK: - Methods

    /**
     * Returns the value for the 'Authorization' HTTP header.
     */
    public String getHeaderValue() {
        return "Bearer " + mToken;
    }

// MARK: - Variables

    private final String mToken;

}
