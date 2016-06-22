package com.roxiemobile.networkingapi.network.rest;

import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity;
import com.roxiemobile.networkingapi.network.rest.response.RestApiError;

public interface Callback<Ti, To>
{
    /**
     * TODO
     */
    boolean onShouldExecute(Call<Ti> call);

    /**
     * TODO
     */
    void onResponse(Call<Ti> call, ResponseEntity<To> entity);

    /**
     * TODO
     */
    void onFailure(Call<Ti> call, RestApiError error);

    /**
     * TODO
     */
    void onCancel(Call<Ti> call);
}
