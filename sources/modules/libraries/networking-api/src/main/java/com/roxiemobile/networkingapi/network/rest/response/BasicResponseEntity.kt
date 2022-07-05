package com.roxiemobile.networkingapi.network.rest.response

import com.roxiemobile.networkingapi.network.http.CookieStore
import com.roxiemobile.networkingapi.network.http.HttpHeaders
import com.roxiemobile.networkingapi.network.http.HttpStatus
import com.roxiemobile.networkingapi.network.http.MediaType
import com.roxiemobile.networkingapi.network.rest.request.BasicRequestEntity
import java.net.URI

class BasicResponseEntity<T>: BasicRequestEntity<T>, ResponseEntity<T> {

// MARK: - Construction

    private constructor(builder: Builder<T>): super(builder) {
        this.httpStatus = requireNotNull(builder.httpStatus) { "httpStatus is null" }
        this.mediaType = requireNotNull(builder.mediaType) { "mediaType is null" }
    }

// MARK: - Properties

    override val httpStatus: HttpStatus

    override val mediaType: MediaType

// MARK: - Inner Types

    class Builder<T>: BasicRequestEntity.Builder<T> {

        constructor() {
            // Do nothing
        }

        constructor(responseEntity: ResponseEntity<T>): super(responseEntity) {
            this.httpStatus = responseEntity.httpStatus
            this.mediaType = responseEntity.mediaType
        }

        internal var httpStatus: HttpStatus? = null
            private set

        internal var mediaType: MediaType? = null
            private set

        override fun link(link: URI): Builder<T> {
            return super.link(link) as Builder<T>
        }

        override fun httpHeaders(httpHeaders: HttpHeaders?): Builder<T> {
            return super.httpHeaders(httpHeaders) as Builder<T>
        }

        override fun cookieStore(cookieStore: CookieStore?): Builder<T> {
            return super.cookieStore(cookieStore) as Builder<T>
        }

        override fun body(body: T?): Builder<T> {
            return super.body(body) as Builder<T>
        }

        fun httpStatus(httpStatus: HttpStatus): Builder<T> {
            this.httpStatus = httpStatus
            return this
        }

        fun mediaType(mediaType: MediaType): Builder<T> {
            this.mediaType = mediaType
            return this
        }

        override fun build(): ResponseEntity<T> {
            return BasicResponseEntity(this)
        }

        companion object {

            @JvmOverloads
            @JvmStatic
            fun <Ti, To> of(responseEntity: ResponseEntity<Ti>, body: To? = null): Builder<To> {
                return Builder<To>().apply {
                    link(responseEntity.link)
                    httpHeaders(responseEntity.httpHeaders)
                    cookieStore(responseEntity.cookieStore)
                    httpStatus(responseEntity.httpStatus)
                    mediaType(responseEntity.mediaType)
                    body(body)
                }
            }
        }
    }

// MARK: - Companion

    companion object {

        @JvmOverloads
        @JvmStatic
        fun <Ti, To> of(responseEntity: ResponseEntity<Ti>, body: To? = null): ResponseEntity<To> {
            return Builder.of(responseEntity, body).build()
        }
    }
}
