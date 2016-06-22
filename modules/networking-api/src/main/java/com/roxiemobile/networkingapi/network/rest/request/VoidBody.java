package com.roxiemobile.networkingapi.network.rest.request;

import com.roxiemobile.networkingapi.network.http.MediaType;
import com.roxiemobile.networkingapi.network.rest.HttpBody;

public class VoidBody implements HttpBody
{
// MARK: - Construction

    private VoidBody() {
        // Do nothing
    }

// MARK: - Methods

    @Override
    public MediaType mediaType() {
        return null;
    }

    @Override
    public byte[] body() {
        return null;
    }

}
