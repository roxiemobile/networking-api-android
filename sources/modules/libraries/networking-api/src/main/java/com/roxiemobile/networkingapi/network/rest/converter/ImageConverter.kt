@file:Suppress("unused")

package com.roxiemobile.networkingapi.network.rest.converter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.roxiemobile.networkingapi.network.http.MediaType
import com.roxiemobile.networkingapi.network.rest.response.BasicResponseEntity
import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity
import com.roxiemobile.networkingapi.network.rest.response.error.nested.ConversionException

class ImageConverter: AbstractCallResultConverter<Bitmap>() {

// MARK: - Methods

    @Throws(ConversionException::class)
    override fun convert(responseEntity: ResponseEntity<ByteArray>): ResponseEntity<Bitmap> {

        var newBody: Bitmap? = null
        try {

            val responseBody = responseEntity.body

            // Try to convert HTTP response to Bitmap
            if (responseBody?.isNotEmpty() == true) {
                newBody = BitmapFactory.decodeByteArray(responseBody, 0, responseBody.size)
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
            MediaType.IMAGE_JPEG,
            MediaType.IMAGE_PNG,
        )
    }
}
