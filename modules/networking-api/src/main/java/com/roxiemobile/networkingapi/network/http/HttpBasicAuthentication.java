/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.roxiemobile.networkingapi.network.http;

import android.support.annotation.NonNull;
import android.util.Base64;

import static com.roxiemobile.androidcommons.diagnostics.Require.requireNotEmpty;

/**
 * Represents HTTP Basic Authentication.
 * @see <a href="http://www.ietf.org/rfc/rfc2617.txt">RFC2617</a>
 */
public class HttpBasicAuthentication extends HttpAuthentication
{
// MARK: - Construction

    public HttpBasicAuthentication(@NonNull String username, @NonNull String password) {
        requireNotEmpty(username, "username is empty");
        requireNotEmpty(password, "password is empty");

        // Init instance variables
        mUsername = username;
        mPassword = password;
    }

// MARK: - Methods

    /**
     * Returns the value for the 'Authorization' HTTP header.
     */
    public String getHeaderValue() {
        byte[] bytes = (mUsername + ":" + mPassword).getBytes();
        return "Basic " + Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

// MARK: - Variables

    private final String mUsername;
    private final String mPassword;

}
