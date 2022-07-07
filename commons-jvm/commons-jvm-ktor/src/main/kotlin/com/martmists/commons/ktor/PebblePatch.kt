package com.martmists.commons.ktor

import com.mitchellbosecke.pebble.PebbleEngine
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.pebble.*
import java.io.StringWriter

/*
 * A custom Pebble instance that doesn't provide a content length header.
 */
val PebblePatch: ApplicationPlugin<PebbleEngine.Builder> = createApplicationPlugin(
    "Pebble",
    { PebbleEngine.Builder() }
) {
    val engine = pluginConfig.build()

    fun process(content: PebbleContent): OutgoingContent = with(content) {
        val writer = StringWriter()
        engine.getTemplate(content.template).evaluate(writer, model, locale)

        val result = NullTextContent(text = writer.toString(), contentType)
        if (etag != null) {
            result.versions += EntityTagVersion(etag!!)
        }
        return result
    }

    onCallRespond { _, value ->
        if (value is PebbleContent) {
            transformBody {
                process(value)
            }
        }
    }
}
