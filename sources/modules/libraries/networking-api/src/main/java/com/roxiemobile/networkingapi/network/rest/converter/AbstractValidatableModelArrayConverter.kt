@file:Suppress("unused")

package com.roxiemobile.networkingapi.network.rest.converter

import com.roxiemobile.androidcommons.data.mapper.DataMapper
import com.roxiemobile.androidcommons.data.model.ValidatableModel
import com.roxiemobile.networkingapi.network.rest.response.BasicResponseEntity
import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity
import com.roxiemobile.networkingapi.network.rest.response.error.nested.ConversionException
import java.io.ByteArrayInputStream
import java.io.InputStreamReader

abstract class AbstractValidatableModelArrayConverter<T: ValidatableModel>:
    AbstractCallResultConverter<Array<T>> {

// MARK: - Construction

    protected constructor(classOfType: Class<Array<T>>) {
        _classOfType = classOfType
    }

// MARK: - Methods

    @Throws(ConversionException::class)
    override fun convert(responseEntity: ResponseEntity<ByteArray>): ResponseEntity<Array<T>> {

        var newBody: Array<T>? = null
        try {

            val responseBody = responseEntity.body

            // Try to convert HTTP response to POJO
            if (responseBody?.isNotEmpty() == true) {

                ByteArrayInputStream(responseBody).use { stream ->
                    InputStreamReader(stream).use { reader ->
                        newBody = DataMapper.fromJson(reader, _classOfType)
                    }
                }
            }
        }
        catch (ex: Exception) {
            throw ConversionException(responseEntity, ex)
        }

        // Create new response entity
        return BasicResponseEntity.of(responseEntity, newBody)
    }

// MARK: - Variables

    private val _classOfType: Class<Array<T>>
}
