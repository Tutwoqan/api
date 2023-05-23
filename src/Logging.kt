package io.github.totwoqan

import java.io.File

interface Logger {
    fun trace(message: String)
    fun buildTrace(): LogMessageBuilder
    fun trace(builder: LogMessageBuilder.() -> Unit)

    fun debug(message: String)
    fun buildDebug(): LogMessageBuilder
    fun debug(builder: LogMessageBuilder.() -> Unit)

    fun info(message: String)
    fun buildInfo(): LogMessageBuilder
    fun info(builder: LogMessageBuilder.() -> Unit)

    fun warning(message: String)
    fun buildWarning(): LogMessageBuilder
    fun warning(builder: LogMessageBuilder.() -> Unit)

    fun error(message: String)
    fun buildError(): LogMessageBuilder
    fun error(builder: LogMessageBuilder.() -> Unit)
    fun error(error: Throwable)

    fun fatal(message: String)
    fun buildFatal(): LogMessageBuilder
    fun fatal(builder: LogMessageBuilder.() -> Unit)
    fun fatal(error: Throwable)
}

interface LogMessageBuilder {
    fun finish()
    fun text(s0: String, vararg sN: String): LogMessageBuilder
    fun newline(): LogMessageBuilder = this.text("\n")
    fun file(file: File): LogMessageBuilder
    fun file(file: File, relativeTo: File): LogMessageBuilder
    fun posInFile(file: File, line: UInt, column: UInt): LogMessageBuilder
    fun posInFile(file: File, relativeTo: File, line: UInt, column: UInt): LogMessageBuilder
    fun exception(exc: Throwable): LogMessageBuilder
    fun indent(width: UInt): LogMessageBuilder
    fun indent(): LogMessageBuilder = this.indent(1u)
}

object FatalError : Throwable("This throwable is for internal purposes, if you see it uncaught, something went very wrong") {
    override fun fillInStackTrace(): Throwable = this
}