package com.martmists.commons.ktor.ext

import io.ktor.server.request.*
import java.util.Locale

fun ApplicationRequest.locale() : Locale {
    return if ((acceptLanguage() ?: "default") == "default") {
        Locale.setDefault(Locale("en"))
        Locale.getDefault()
    } else {
        val languageRanges = Locale.LanguageRange.parse(acceptLanguage())
        Locale.lookup(languageRanges, Locale.getAvailableLocales().toList())
    }
}
