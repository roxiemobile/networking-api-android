package com.roxiemobile.networkingapi.network.rest.interceptor

import com.roxiemobile.networkingapi.network.rest.RestApiClient.HttpResponseException
import okhttp3.Response

class ShortCircuitRedirectInterceptor: AbstractRedirectInterceptor() {

// MARK: - Methods

    override fun onRedirect(response: Response): Response {
        // Throw an exception on redirects
        throw HttpResponseException(decompressResponse(response))
    }
}
