package com.roxiemobile.networkingapi.network.rest;

import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity;
import com.roxiemobile.networkingapi.network.rest.response.RestApiError;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CallbackDecorator<Ti, To> implements Callback<Ti, To> {

// MARK: - Construction

    public CallbackDecorator(@Nullable Callback<Ti, To> callback) {
        mCallback = callback;
    }

    public CallbackDecorator() {
        mCallback = null;
    }

// MARK: - Methods

    @Override
    public boolean onShouldExecute(@NotNull Call<Ti> call) {
        return (mCallback == null) || mCallback.onShouldExecute(call);
    }

    @Override
    public void onSuccess(@NotNull Call<Ti> call, @NotNull ResponseEntity<To> responseEntity) {
        if (mCallback != null) {
            mCallback.onSuccess(call, responseEntity);
        }
    }

    @Override
    public void onFailure(@NotNull Call<Ti> call, @NotNull RestApiError error) {
        if (mCallback != null) {
            mCallback.onFailure(call, error);
        }
    }

    @Override
    public void onCancel(@NotNull Call<Ti> call) {
        if (mCallback != null) {
            mCallback.onCancel(call);
        }
    }

// MARK: - Variables

    private final @Nullable Callback<Ti, To> mCallback;
}
