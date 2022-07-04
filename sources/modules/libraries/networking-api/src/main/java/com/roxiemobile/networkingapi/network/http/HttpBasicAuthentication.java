package com.roxiemobile.networkingapi.network.http;

import android.util.Base64;

import com.roxiemobile.androidcommons.diagnostics.Guard;

import org.jetbrains.annotations.NotNull;

/**
 * Represents HTTP Basic Authentication.
 *
 * @see <a href="http://www.ietf.org/rfc/rfc2617.txt">RFC2617</a>
 */
public final class HttpBasicAuthentication extends HttpAuthentication {

// MARK: - Construction

    public HttpBasicAuthentication(@NotNull String username, @NotNull String password) {
        Guard.notEmpty(username, "username is empty");
        Guard.notEmpty(password, "password is empty");

        // Init instance variables
        mUsername = username;
        mPassword = password;
    }

// MARK: - Methods

    /**
     * Returns the value for the 'Authorization' HTTP header.
     */
    public @NotNull String getHeaderValue() {
        byte[] bytes = (mUsername + ":" + mPassword).getBytes();
        return "Basic " + Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

// MARK: - Variables

    private final @NotNull String mUsername;
    private final @NotNull String mPassword;
}
