package com.roxiemobile.networkingapi.network.rest.request

import com.roxiemobile.networkingapi.network.http.CookieStore
import com.roxiemobile.networkingapi.network.http.HttpHeaders
import com.roxiemobile.networkingapi.network.http.InMemoryCookieStore
import java.net.URI

open class BasicRequestEntity<TBody>: RequestEntity<TBody> {

// MARK: - Construction

    protected constructor(builder: Builder<TBody>) {
        this.link = requireNotNull(builder.link) { "link is null" }
        this.httpHeaders = builder.httpHeaders ?: HttpHeaders()
        this.cookieStore = builder.cookieStore ?: InMemoryCookieStore()
        this.body = builder.body
    }

// MARK: - Properties

    final override val link: URI

    final override val httpHeaders: HttpHeaders

    final override val cookieStore: CookieStore

    final override val body: TBody?

// MARK: - Methods

    override fun clone(): RequestEntity<TBody> {
        return Builder(this).build()
    }

// MARK: - Inner Types

    open class Builder<TBody> {

        constructor(requestEntity: RequestEntity<TBody>? = null) {
            this.link = requestEntity?.link
            this.httpHeaders = requestEntity?.httpHeaders
            this.cookieStore = requestEntity?.cookieStore
            this.body = requestEntity?.body
        }

        internal var link: URI? = null
            private set

        internal var httpHeaders: HttpHeaders? = null
            private set

        internal var cookieStore: CookieStore? = null
            private set

        internal var body: TBody? = null
            private set

        open fun link(link: URI): Builder<TBody> {
            this.link = link
            return this
        }

        open fun httpHeaders(httpHeaders: HttpHeaders): Builder<TBody> {
            this.httpHeaders = httpHeaders
            return this
        }

        open fun cookieStore(cookieStore: CookieStore): Builder<TBody> {
            this.cookieStore = cookieStore
            return this
        }

        open fun body(body: TBody?): Builder<TBody> {
            this.body = body
            return this
        }

        open fun build(): RequestEntity<TBody> {
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
