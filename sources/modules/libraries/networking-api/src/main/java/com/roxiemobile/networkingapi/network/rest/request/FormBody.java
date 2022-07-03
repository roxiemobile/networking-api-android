package com.roxiemobile.networkingapi.network.rest.request;

import android.text.TextUtils;

import com.annimon.stream.Stream;
import com.roxiemobile.androidcommons.data.Constants.Charsets;
import com.roxiemobile.androidcommons.logging.Logger;
import com.roxiemobile.androidcommons.util.StringUtils;
import com.roxiemobile.networkingapi.network.http.MediaType;
import com.roxiemobile.networkingapi.network.rest.HttpBody;

import org.jetbrains.annotations.NotNull;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class FormBody implements HttpBody {

// MARK: - Construction

    private FormBody(@NotNull Builder builder) {
        mBody = builder.toByteArray();
    }

// MARK: - Methods

    @Override
    public @NotNull MediaType mediaType() {
        return MediaType.APPLICATION_FORM_URLENCODED;
    }

    @Override
    public @NotNull byte[] body() {
        return mBody;
    }

// MARK: - Inner types

    public static final class Builder {

        public Builder put(@NotNull String name, @NotNull String value) {
            mValues.put(name.trim(), value.trim());
            return this;
        }

        public @NotNull FormBody build() {
            return new FormBody(this);
        }

        private @NotNull byte[] toByteArray() {
            List<String> values = new ArrayList<>();

            final String charsetName = Charsets.UTF_8.name();
            Stream.of(mValues.entrySet())
                    .filter(entry -> StringUtils.isNotEmpty(entry.getKey()))
                    .forEach(entry -> {
                        try {
                            String key = URLEncoder.encode(entry.getKey(), charsetName);
                            String value = URLEncoder.encode(entry.getValue(), charsetName);
                            values.add(key + '=' + value);
                        }
                        catch (UnsupportedEncodingException e) {
                            Logger.e(TAG, e);
                        }
                    });

            return TextUtils.join("&", values).getBytes();
        }

        private final @NotNull HashMap<String, String> mValues = new HashMap<>();
    }

// MARK: - Constants

    public static final @NotNull String TAG = FormBody.class.getSimpleName();

// MARK: - Variables

    private final @NotNull byte[] mBody;
}
