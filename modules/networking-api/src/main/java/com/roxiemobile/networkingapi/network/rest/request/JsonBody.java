package com.roxiemobile.networkingapi.network.rest.request;

import com.google.gson.JsonElement;
import com.roxiemobile.androidcommons.data.mapper.DataMapper;
import com.roxiemobile.networkingapi.network.HttpKeys.CharsetName;
import com.roxiemobile.networkingapi.network.http.MediaType;
import com.roxiemobile.networkingapi.network.rest.HttpBody;

public class JsonBody implements HttpBody
{
// MARK: - Construction

    public static JsonBody build(JsonElement element) {
        return new JsonBody(element);
    }

    private JsonBody(JsonElement body) {
        mBody = body;
    }

// MARK: - Methods

    @Override
    public MediaType mediaType() {
        return MEDIA_TYPE_JSON;
    }

    @Override
    public byte[] body() {
        return (mBody != null) ? DataMapper.toByteArray(mBody) : null;
    }

// MARK: - Constants

    private final static MediaType MEDIA_TYPE_JSON =
            MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE + "; charset=" + CharsetName.UTF_8);

// MARK: - Variables

    private JsonElement mBody;

}
