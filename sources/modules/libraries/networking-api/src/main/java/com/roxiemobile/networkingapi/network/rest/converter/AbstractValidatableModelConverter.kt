@file:Suppress("unused")

package com.roxiemobile.networkingapi.network.rest.converter

import com.google.gson.JsonIOException
import com.google.gson.JsonSyntaxException
import com.roxiemobile.androidcommons.data.mapper.DataMapper
import com.roxiemobile.androidcommons.data.model.ValidatableModel
import com.roxiemobile.networkingapi.network.rest.response.BasicResponseEntity
import com.roxiemobile.networkingapi.network.rest.response.ResponseEntity
import com.roxiemobile.networkingapi.network.rest.response.error.nested.ConversionException
import java.io.ByteArrayInputStream
import java.io.InputStreamReader

abstract class AbstractValidatableModelConverter<T: ValidatableModel>:
    AbstractCallResultConverter<T> {

// MARK: - Construction

    protected constructor(classOfType: Class<T>) {
        _classOfType = classOfType
    }

// MARK: - Methods

    @Throws(ConversionException::class)
    override fun convert(responseEntity: ResponseEntity<ByteArray>): ResponseEntity<T> {

        var newBody: T? = null
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
            when (ex) {
                is JsonIOException,
                is JsonSyntaxException -> throw ConversionException(responseEntity, ex)
                else -> throw ex
            }
        }

        // Create new response entity
        return BasicResponseEntity.of(responseEntity, newBody)
    }

// MARK: - Variables

    private var _classOfType: Class<T>
}
