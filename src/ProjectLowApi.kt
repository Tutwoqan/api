package io.github.totwoqan

import java.io.File

interface ProjectLowApi {
    /**
     * Creates pipe bound to currently initializing task.
     * @throws IllegalStateException if no tasks are initializing at call time
     */
    fun createPipe(): Pair<FilesPipe, FilesPipeWriter>
    fun join(pipe0: FilesPipe, vararg pipeN: FilesPipe)
    fun filter(pipe: FilesPipe, filter: (File) -> Boolean)

//    val variables: ScopesAccessor<Any?>
//    val tasks: ScopesAccessor<Task>

    //    val hostOs: OS
    // val hostArch: String // todo

    /**
     * Returns new unique directory for this load. It is not cleared between reruns, but removed on config reload.
     *
     * Each invocation will return new directory.
     */
    fun offerTemporaryBuildDir(): File

    /**
     * Returns build directory for specified package. It is not cleared between reruns and reloads and by clean tasks,
     * but will be removed by full clean.
     */
    fun buildDirForPackage(pkgName: String): File
}