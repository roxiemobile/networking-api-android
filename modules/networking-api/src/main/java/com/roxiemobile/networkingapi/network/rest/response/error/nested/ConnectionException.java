package com.roxiemobile.networkingapi.network.rest.response.error.nested;

public class ConnectionException extends Exception
{
// MARK: - Construction

    /**
     * Construct a new instance of {@code ConnectionException}.
     */
    public ConnectionException(Throwable cause) {
        super(cause);
    }

// MARK: - Constants

    private static final long serialVersionUID = 6805154194145428738L;
}
