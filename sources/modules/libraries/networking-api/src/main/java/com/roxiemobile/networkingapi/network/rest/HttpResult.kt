package com.roxiemobile.networkingapi.network.rest

import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity

@Deprecated("Must be removed", ReplaceWith("kotlin.Result"))
class HttpResult: Result<ResponseEntity<ByteArray>, Throwable, ByteArray> {

// MARK: - Construction

    private constructor(responseEntity: ResponseEntity<ByteArray>): super(responseEntity)

    private constructor(error: Throwable): super(error)

// MARK: - Companion

    companion object {

        fun success(responseEntity: ResponseEntity<ByteArray>): HttpResult {
            return HttpResult(responseEntity)
        }

        fun failure(error: Throwable): HttpResult {
            return HttpResult(error)
        }
    }
}
