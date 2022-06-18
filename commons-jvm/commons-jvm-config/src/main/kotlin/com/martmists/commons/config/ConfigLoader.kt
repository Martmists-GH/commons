package com.martmists.commons.config

import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File
import java.io.InputStream

class ConfigLoader(createMapper: () -> ObjectMapper) {
    private val mapper = createMapper()

    inline fun <reified T> load(path: String, resource: InputStream) = load<T>(path, resource.readAllBytes().decodeToString())
    inline fun <reified T> load(path: String, default: String) = load<T>(File(path), default)
    inline fun <reified T> load(file: File, resource: InputStream) = load<T>(file, resource.readAllBytes().decodeToString())
    inline fun <reified T> load(file: File, default: String) = load<T>(file, T::class.java, default)

    @PublishedApi
    internal fun <T> load(file: File, type: Class<T>, default: String): T {
        if (!file.exists()) {
            file.createNewFile()
            file.writeText(default)
        }
        return mapper.readValue(file, type) as T
    }

    fun <T> save(path: String, obj: T) = save(File(path), obj)
    fun <T> save(file: File, obj: T) {
        mapper.writeValue(file, obj)
    }
}
