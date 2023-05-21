package io.github.totwoqan

interface Logger {
    fun trace(message: String)
    fun trace(lazyMessage: () -> String)
    fun debug(message: String)
    fun debug(lazyMessage: () -> String)
    fun info(message: String)
    fun info(lazyMessage: () -> String)
    fun warning(message: String)
    fun warning(lazyMessage: () -> String)
    fun error(message: String)
    fun error(lazyMessage: () -> String)
    fun error(error: Throwable)
    fun fatal(message: String)
    fun fatal(lazyMessage: () -> String)
    fun fatal(error: Throwable)
}