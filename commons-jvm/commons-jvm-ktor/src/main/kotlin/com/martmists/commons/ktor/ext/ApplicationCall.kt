package com.martmists.commons.ktor.ext

import io.ktor.server.application.*
import io.ktor.server.pebble.*
import io.ktor.server.response.*

suspend fun ApplicationCall.respondTemplate(template: String, properties: Map<String, Any> = emptyMap()) {
    respond(PebbleContent(template, properties, locale = request.locale()))
}
