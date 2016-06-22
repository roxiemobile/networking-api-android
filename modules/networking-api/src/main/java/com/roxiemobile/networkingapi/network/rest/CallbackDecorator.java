package com.roxiemobile.networkingapi.network.rest;

import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity;
import com.roxiemobile.networkingapi.network.rest.response.RestApiError;

public class CallbackDecorator<Ti, To> implements Callback<Ti, To>
{
// MARK: - Construction

    public CallbackDecorator(Callback<Ti, To> callback) {
        mCallback = callback;
    }

    public CallbackDecorator() {
        mCallback = null;
    }

// MARK: - Methods

    @Override
    public boolean onShouldExecute(Call<Ti> call) {
        return (mCallback == null) || mCallback.onShouldExecute(call);
    }

    @Override
    public void onResponse(Call<Ti> call, ResponseEntity<To> entity) {
        if (mCallback != null) {
            mCallback.onResponse(call, entity);
        }
    }

    @Override
    public void onFailure(Call<Ti> call, RestApiError error) {
        if (mCallback != null) {
            mCallback.onFailure(call, error);
        }
    }

    @Override
    public void onCancel(Call<Ti> call) {
        if (mCallback != null) {
            mCallback.onCancel(call);
        }
    }

// MARK: - Variables

    private final Callback<Ti, To> mCallback;

}
