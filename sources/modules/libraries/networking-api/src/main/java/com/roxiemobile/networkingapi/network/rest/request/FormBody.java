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
        _body = builder.toByteArray();
    }

// MARK: - Methods

    @Override
    public @NotNull MediaType getMediaType() {
        return MediaType.APPLICATION_FORM_URLENCODED;
    }

    @Override
    public @NotNull byte[] getBody() {
        return _body;
    }

// MARK: - Inner types

    public static final class Builder {

        public Builder put(@NotNull String name, @NotNull String value) {
            _values.put(name.trim(), value.trim());
            return this;
        }

        public @NotNull FormBody build() {
            return new FormBody(this);
        }

        private @NotNull byte[] toByteArray() {
            @NotNull List<String> values = new ArrayList<>();

            @NotNull String charsetName = Charsets.UTF_8.name();
            Stream.of(_values.entrySet())
                    .filter(entry -> StringUtils.isNotEmpty(entry.getKey()))
                    .forEach(entry -> {
                        try {
                            @NotNull String key = URLEncoder.encode(entry.getKey(), charsetName);
                            @NotNull String value = URLEncoder.encode(entry.getValue(), charsetName);
                            values.add(key + '=' + value);
                        }
                        catch (UnsupportedEncodingException ex) {
                            Logger.w(TAG, ex);
                        }
                    });

            return TextUtils.join("&", values).getBytes();
        }

        public static final @NotNull String TAG = Builder.class.getSimpleName();

        private final @NotNull HashMap<String, String> _values = new HashMap<>();
    }

// MARK: - Variables

    private final @NotNull byte[] _body;
}
