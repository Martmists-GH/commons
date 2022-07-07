package com.martmists.commons.ktor

import io.ktor.http.*
import io.ktor.http.content.*

/*
 * Custom Text Content type with null contentLength
 */
class NullTextContent(
    private val text: String,
    override val contentType: ContentType,
    override val status: HttpStatusCode? = null
) : OutgoingContent.ByteArrayContent() {
    private val bytes = text.toByteArray(contentType.charset() ?: Charsets.UTF_8)

    override val contentLength: Long? = null

    override fun bytes(): ByteArray = bytes

    override fun toString(): String = "TextContent[$contentType] \"${text.take(30)}\""
}
