@file:Suppress("unused")

package com.roxiemobile.networkingapi.network.rest.converter

import com.roxiemobile.networkingapi.network.http.MediaType
import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity
import com.roxiemobile.networkingapi.network.rest.response.error.nested.ConversionException

class PassThroughConverter: AbstractCallResultConverter<ByteArray>() {

// MARK: - Methods

    @Throws(ConversionException::class)
    override fun convert(responseEntity: ResponseEntity<ByteArray>): ResponseEntity<ByteArray> {
        return responseEntity
    }

    override fun supportedMediaTypes(): Array<MediaType> {
        return SUPPORTED_MEDIA_TYPES
    }

// MARK: - Companion

    companion object {

        private val SUPPORTED_MEDIA_TYPES = arrayOf(
            MediaType.ALL,
        )
    }
}
