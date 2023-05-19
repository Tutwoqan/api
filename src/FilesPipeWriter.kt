package io.github.totwoqan

import java.io.File

/**
 * Input end of [files pipe][FilesPipe].
 * Bound to specified task and can be filled only when owner task is executing.
 * Attempts to fill before or after executing owner task will cause [IllegalStateException].
 *
 * @see FilesPipe
 */
interface FilesPipeWriter {
    fun add(file0: File, vararg fileN: File)
    fun add(fileA0: Array<File>, vararg fileAN: Array<File>)
    fun add(fileI0: Iterable<File>, vararg fileIN: Iterable<File>)
    fun add(fileS0: Set<File>, vararg fileSN: Set<File>)
}