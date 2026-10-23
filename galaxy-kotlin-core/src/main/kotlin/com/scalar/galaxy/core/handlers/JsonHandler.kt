@file:JvmName("JsonHandler")

package com.scalar.galaxy.core.handlers

import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import com.scalar.galaxy.core.http.HttpResponse
import com.scalar.galaxy.core.http.HttpResponse.Handler
import com.scalar.galaxy.errors.GalaxyInvalidDataException

internal inline fun <reified T> jsonHandler(jsonMapper: JsonMapper): Handler<T> =
    object : Handler<T> {
        override fun handle(response: HttpResponse): T {
            try {
                return jsonMapper.readValue(response.body(), jacksonTypeRef())
            } catch (e: Exception) {
                throw GalaxyInvalidDataException("Error reading response", e)
            }
        }
    }
