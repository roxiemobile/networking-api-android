package com.roxiemobile.networkingapi.network.rest.request

import com.roxiemobile.networkingapi.network.http.CookieStore
import com.roxiemobile.networkingapi.network.http.HttpHeaders
import java.net.URI

open class BasicRequestEntity<T>: RequestEntity<T> {

// MARK: - Construction

    protected constructor(builder: Builder<T>) {
        this.link = requireNotNull(builder.link) { "link is null" }
        this.httpHeaders = builder.httpHeaders
        this.cookieStore = builder.cookieStore
        this.body = builder.body
    }

// MARK: - Properties

    final override val link: URI

    final override val httpHeaders: HttpHeaders?

    final override val cookieStore: CookieStore?

    final override val body: T?

// MARK: - Inner Types

    open class Builder<T> {

        constructor() {
            // Do nothing
        }

        constructor(requestEntity: RequestEntity<T>) {
            this.link = requestEntity.link
            this.httpHeaders = requestEntity.httpHeaders
            this.cookieStore = requestEntity.cookieStore
            this.body = requestEntity.body
        }

        internal var link: URI? = null
            private set

        internal var httpHeaders: HttpHeaders? = null
            private set

        internal var cookieStore: CookieStore? = null
            private set

        internal var body: T? = null
            private set

        open fun link(link: URI): Builder<T> {
            this.link = link
            return this
        }

        open fun httpHeaders(httpHeaders: HttpHeaders?): Builder<T> {
            this.httpHeaders = httpHeaders
            return this
        }

        open fun cookieStore(cookieStore: CookieStore?): Builder<T> {
            this.cookieStore = cookieStore
            return this
        }

        open fun body(body: T?): Builder<T> {
            this.body = body
            return this
        }

        open fun build(): RequestEntity<T> {
            return BasicRequestEntity(this)
        }

        companion object {

            @JvmOverloads
            @JvmStatic
            fun <Ti, To> of(requestEntity: RequestEntity<Ti>, body: To? = null): Builder<To> {
                return Builder<To>().apply {
                    link(requestEntity.link)
                    httpHeaders(requestEntity.httpHeaders)
                    cookieStore(requestEntity.cookieStore)
                    body(body)
                }
            }
        }
    }

// MARK: - Companion

    companion object {

        @JvmOverloads
        @JvmStatic
        fun <Ti, To> of(requestEntity: RequestEntity<Ti>, body: To? = null): RequestEntity<To> {
            return Builder.of(requestEntity, body).build()
        }
    }
}
