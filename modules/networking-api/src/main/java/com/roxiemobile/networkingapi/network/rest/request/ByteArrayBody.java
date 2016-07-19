package com.roxiemobile.networkingapi.network.rest.request;

import com.roxiemobile.networkingapi.network.http.MediaType;
import com.roxiemobile.networkingapi.network.rest.HttpBody;

public class ByteArrayBody implements HttpBody
{
// MARK: - Construction

    public ByteArrayBody(byte[] body) {
        mBody = body;
    }

// MARK: - Methods

    @Override
    public MediaType mediaType() {
        return MediaType.APPLICATION_OCTET_STREAM;
    }

    @Override
    public byte[] body() {
        return mBody;
    }

// MARK: - Variables

    private byte[] mBody;

}
