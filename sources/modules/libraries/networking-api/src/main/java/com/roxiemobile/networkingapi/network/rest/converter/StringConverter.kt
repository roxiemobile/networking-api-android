package com.roxiemobile.networkingapi.network.rest.converter

import com.roxiemobile.networkingapi.network.http.MediaType
import com.roxiemobile.networkingapi.network.rest.response.BasicResponseEntity
import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity
import com.roxiemobile.networkingapi.network.rest.response.error.nested.ConversionException

class StringConverter: AbstractCallResultConverter<String>() {

// MARK: - Methods

    @Throws(ConversionException::class)
    override fun convert(responseEntity: ResponseEntity<ByteArray>): ResponseEntity<String> {

        var newBody: String? = null
        try {

            val responseBody = responseEntity.body

            // Try to convert HTTP response to string
            if (responseBody?.isNotEmpty() == true) {

                val charset = responseEntity.mediaType.getCharset(Charsets.UTF_8)
                newBody = String(responseBody, charset)
            }
        }
        catch (ex: Exception) {
            throw ConversionException(responseEntity, ex)
        }

        // Create new response entity
        return BasicResponseEntity.of(responseEntity, newBody)
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
