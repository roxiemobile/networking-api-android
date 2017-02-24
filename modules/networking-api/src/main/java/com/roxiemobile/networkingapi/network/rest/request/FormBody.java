package com.roxiemobile.networkingapi.network.rest.request;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.annimon.stream.Stream;
import com.roxiemobile.androidcommons.logging.Logger;
import com.roxiemobile.androidcommons.util.StringUtils;
import com.roxiemobile.networkingapi.network.HttpKeys.CharsetName;
import com.roxiemobile.networkingapi.network.http.MediaType;
import com.roxiemobile.networkingapi.network.rest.HttpBody;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FormBody implements HttpBody
{
// MARK: - Construction

    private FormBody(Builder builder) {
        mBody = builder.toByteArray();
    }

// MARK: - Methods

    @Override
    public MediaType mediaType() {
        return MediaType.APPLICATION_FORM_URLENCODED;
    }

    @Override
    public @NonNull byte[] body() {
        return mBody;
    }

// MARK: - Inner types

    public static final class Builder
    {
        public Builder put(@NonNull String name, @NonNull String value) {
            mValues.put(name.trim(), value.trim());
            return this;
        }

        public @NonNull FormBody build() {
            return new FormBody(this);
        }

        private byte[] toByteArray() {
            List<String> values = new ArrayList<>();

            Stream.of(mValues.entrySet())
                  .filter(entry -> StringUtils.isNotEmpty(entry.getKey()))
                  .forEach(entry -> {
                      try {
                          String key = URLEncoder.encode(entry.getKey(), CharsetName.UTF_8);
                          String value = URLEncoder.encode(entry.getValue(), CharsetName.UTF_8);
                          values.add(key + '=' + value);
                      }
                      catch (UnsupportedEncodingException e) {
                          Logger.e(TAG, e);
                      }
                  });

            return TextUtils.join("&", values).getBytes();
        }

        private HashMap<String, String> mValues = new HashMap<>();
    }

// MARK: - Constants

    public static final String TAG = FormBody.class.getSimpleName();

// MARK: - Variables

    private byte[] mBody;
}
