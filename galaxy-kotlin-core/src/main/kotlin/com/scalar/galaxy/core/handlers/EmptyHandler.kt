@file:JvmName("EmptyHandler")

package com.scalar.galaxy.core.handlers

import com.scalar.galaxy.core.http.HttpResponse
import com.scalar.galaxy.core.http.HttpResponse.Handler

internal fun emptyHandler(): Handler<Void?> = EmptyHandlerInternal

private object EmptyHandlerInternal : Handler<Void?> {
    override fun handle(response: HttpResponse): Void? = null
}
