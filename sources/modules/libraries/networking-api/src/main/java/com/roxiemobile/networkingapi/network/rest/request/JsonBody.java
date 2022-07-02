package com.roxiemobile.networkingapi.network.rest.request;

import com.google.gson.JsonElement;
import com.roxiemobile.androidcommons.data.Constants.Charsets;
import com.roxiemobile.androidcommons.data.mapper.DataMapper;
import com.roxiemobile.networkingapi.network.http.MediaType;
import com.roxiemobile.networkingapi.network.rest.HttpBody;

public class JsonBody implements HttpBody {

// MARK: - Construction

    public JsonBody(JsonElement body) {
        mBody = body;
    }

// MARK: - Methods

    @Override
    public MediaType mediaType() {
        return MEDIA_TYPE;
    }

    @Override
    public byte[] body() {
        return (mBody != null) ? DataMapper.toByteArray(mBody) : null;
    }

// MARK: - Constants

    private final static MediaType MEDIA_TYPE =
            MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE + "; charset=" + Charsets.UTF_8.name());

// MARK: - Variables

    private JsonElement mBody;
}
