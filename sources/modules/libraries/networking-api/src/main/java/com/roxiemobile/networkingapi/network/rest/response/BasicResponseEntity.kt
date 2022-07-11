package com.roxiemobile.networkingapi.network.rest.response

import com.roxiemobile.networkingapi.network.http.CookieStore
import com.roxiemobile.networkingapi.network.http.HttpHeaders
import com.roxiemobile.networkingapi.network.http.HttpStatus
import com.roxiemobile.networkingapi.network.http.MediaType
import com.roxiemobile.networkingapi.network.rest.request.BasicRequestEntity
import java.net.URI

class BasicResponseEntity<TBody>: BasicRequestEntity<TBody>, ResponseEntity<TBody> {

// MARK: - Construction

    private constructor(builder: Builder<TBody>): super(builder) {
        this.httpStatus = requireNotNull(builder.httpStatus) { "httpStatus is null" }
        this.mediaType = requireNotNull(builder.mediaType) { "mediaType is null" }
    }

// MARK: - Properties

    override val httpStatus: HttpStatus

    override val mediaType: MediaType

// MARK: - Methods

    override fun clone(): ResponseEntity<TBody> {
        return Builder(this).build()
    }

// MARK: - Inner Types

    class Builder<TBody>: BasicRequestEntity.Builder<TBody> {

        constructor(responseEntity: ResponseEntity<TBody>? = null): super(responseEntity) {
            this.httpStatus = responseEntity?.httpStatus
            this.mediaType = responseEntity?.mediaType
        }

        internal var httpStatus: HttpStatus? = null
            private set

        internal var mediaType: MediaType? = null
            private set

        override fun link(link: URI): Builder<TBody> {
            return super.link(link) as Builder<TBody>
        }

        override fun httpHeaders(httpHeaders: HttpHeaders): Builder<TBody> {
            return super.httpHeaders(httpHeaders) as Builder<TBody>
        }

        override fun cookieStore(cookieStore: CookieStore): Builder<TBody> {
            return super.cookieStore(cookieStore) as Builder<TBody>
        }

        override fun body(body: TBody?): Builder<TBody> {
            return super.body(body) as Builder<TBody>
        }

        fun httpStatus(httpStatus: HttpStatus): Builder<TBody> {
            this.httpStatus = httpStatus
            return this
        }

        fun mediaType(mediaType: MediaType): Builder<TBody> {
            this.mediaType = mediaType
            return this
        }

        override fun build(): ResponseEntity<TBody> {
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
