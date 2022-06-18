package com.martmists.commons.logging

import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager

inline fun <reified T: Any> T.logger() = lazy {
    LogManager.getLogger(T::class.java)
}

inline fun <reified T: Any> T.logger(level: Level) = lazy {
    LogManager.getLogger(T::class.java).atLevel(level)
}
