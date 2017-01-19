package com.roxiemobile.networkingapi.network.rest.request;

import android.support.annotation.NonNull;

import com.roxiemobile.networkingapi.network.http.MediaType;
import com.roxiemobile.networkingapi.network.rest.HttpBody;

import static com.roxiemobile.androidcommons.diagnostics.Require.requireNotNull;

public class ByteArrayBody implements HttpBody
{
// MARK: - Construction

    public ByteArrayBody(@NonNull byte[] body, @NonNull MediaType mediaType) {
        requireNotNull(body, "body is null");
        requireNotNull(mediaType, "mediaType is null");

        // Init instance variables
        mBody = body;
        mMediaType = mediaType;
    }

    public ByteArrayBody(@NonNull byte[] body) {
        this(body, MediaType.APPLICATION_OCTET_STREAM);
    }

    public ByteArrayBody() {
        this(EMPTY_ARRAY);
    }

// MARK: - Methods

    @Override
    public MediaType mediaType() {
        return mMediaType;
    }

    @Override
    public byte[] body() {
        return mBody;
    }

// MARK: - Constants

    private static final byte[] EMPTY_ARRAY = new byte[]{};

// MARK: - Variables

    private final MediaType mMediaType;

    private final byte[] mBody;

}
