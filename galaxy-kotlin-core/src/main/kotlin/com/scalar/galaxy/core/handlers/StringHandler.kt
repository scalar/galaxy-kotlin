@file:JvmName("StringHandler")

package com.scalar.galaxy.core.handlers

import com.scalar.galaxy.core.http.HttpResponse
import com.scalar.galaxy.core.http.HttpResponse.Handler
import com.scalar.galaxy.core.http.boundedBody

internal fun stringHandler(): Handler<String> = StringHandlerInternal

private object StringHandlerInternal : Handler<String> {
    override fun handle(response: HttpResponse): String =
        // Bounded, since the whole body is held as one string.
        response.boundedBody().readBytes().toString(Charsets.UTF_8)
}
