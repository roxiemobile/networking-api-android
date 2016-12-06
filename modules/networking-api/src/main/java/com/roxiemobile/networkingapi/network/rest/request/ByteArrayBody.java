package com.roxiemobile.networkingapi.network.rest.request;

import com.roxiemobile.networkingapi.network.http.MediaType;
import com.roxiemobile.networkingapi.network.rest.HttpBody;

public class ByteArrayBody implements HttpBody
{
// MARK: - Construction

    public ByteArrayBody(byte[] body) {
        mBody = body;
    }

    public ByteArrayBody() {
        mBody = EMPTY_ARRAY;
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

// MARK: - Constants

    private static final byte[] EMPTY_ARRAY = new byte[]{};

// MARK: - Variables

    private byte[] mBody;

}
