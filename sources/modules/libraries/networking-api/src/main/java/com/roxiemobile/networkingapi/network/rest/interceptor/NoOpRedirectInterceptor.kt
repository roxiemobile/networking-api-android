package com.roxiemobile.networkingapi.network.rest.interceptor

import okhttp3.Response

class NoOpRedirectInterceptor: AbstractRedirectInterceptor() {

// MARK: - Methods

    override fun onRedirect(response: Response): Response {
        return response
    }
}
