@file:Suppress("unused")

package com.roxiemobile.networkingapi.network.rest.converter

import com.google.gson.JsonIOException
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import com.roxiemobile.networkingapi.network.http.MediaType
import com.roxiemobile.networkingapi.network.rest.response.BasicResponseEntity
import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity
import com.roxiemobile.networkingapi.network.rest.response.error.nested.ConversionException
import java.io.UnsupportedEncodingException

class JsonObjectConverter: AbstractCallResultConverter<JsonObject>() {

// MARK: - Methods

    @Throws(ConversionException::class)
    override fun convert(responseEntity: ResponseEntity<ByteArray>): ResponseEntity<JsonObject> {

        var newBody: JsonObject? = null
        try {

            val responseBody = responseEntity.body

            // Try to convert HTTP response to JSON object
            if (responseBody?.isNotEmpty() == true) {

                val charset = responseEntity.mediaType.getCharset(Charsets.UTF_8)
                val jsonString = String(responseBody, charset).trim()

                if (jsonString.isNotEmpty()) {
                    newBody = JsonParser().parse(jsonString).asJsonObject
                }
            }
        }
        catch (ex: Exception) {
            when (ex) {
                is IllegalStateException,
                is JsonIOException,
                is JsonSyntaxException,
                is UnsupportedEncodingException -> throw ConversionException(responseEntity, ex)
                else -> throw ex
            }
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
            MediaType.APPLICATION_JSON,
        )
    }
}
