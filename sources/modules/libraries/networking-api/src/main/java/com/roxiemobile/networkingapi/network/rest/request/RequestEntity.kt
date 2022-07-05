package com.roxiemobile.networkingapi.network.rest.request

import com.roxiemobile.networkingapi.network.http.CookieStore
import com.roxiemobile.networkingapi.network.http.HttpHeaders
import java.net.URI

interface RequestEntity<T> {

// MARK: - Properties

    /**
     * TODO
     */
    val link: URI

    /**
     * TODO
     */
    val httpHeaders: HttpHeaders?

    /**
     * TODO
     */
    val cookieStore: CookieStore?

    /**
     * TODO
     */
    val body: T?
}
