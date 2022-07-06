@file:Suppress("JoinDeclarationAndAssignment", "unused")

package com.roxiemobile.networkingapi.network.http.body

import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.roxiemobile.androidcommons.data.mapper.DataMapper
import com.roxiemobile.networkingapi.network.http.MediaType

class GsonBody: HttpBody {

// MARK: - Construction

    @JvmOverloads
    constructor(body: JsonElement = JsonNull.INSTANCE) {
        this.mediaType = MEDIA_TYPE
        this.body = DataMapper.toByteArray(body)
    }

// MARK: - Properties

    override val mediaType: MediaType

    override val body: ByteArray

// MARK: - Companion

    companion object {

        private val MEDIA_TYPE = MediaType
            .valueOf(MediaType.APPLICATION_JSON_VALUE + "; charset=" + Charsets.UTF_8.name())
    }
}
