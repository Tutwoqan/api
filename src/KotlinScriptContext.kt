package io.github.totwoqan

import java.io.File
import java.net.URL

abstract class KotlinScriptContext {
    abstract fun <M : TaskType<C, T>, C : AbstractTaskConfig, T : Task> createTask(taskType: M, initializer: C.() -> Unit): T

    abstract fun assertExists(file0: File, vararg fileN: File): FilesPipe

    abstract fun download(url: String): FilesPipe

    abstract fun download(url: URL): FilesPipe

    abstract fun collectFiles(dir: File): FilesPipe

    abstract fun collectFiles(dir: File, filter: (File) -> Boolean): FilesPipe

    abstract fun extendClasspath(jarsOrDirs: FilesPipe)

    abstract val logger: Logger

    abstract fun offerTemporaryBuildDir(): File

    operator fun File.div(subPath: String) = this.resolve(subPath)

    abstract val currentDirectory: File

    abstract val projectRootDir: File

    abstract val globalVariables: VariableScopes

    abstract val tasks: TasksScopes

    abstract fun FilesPipe.filter(predicate: (File) -> Boolean): FilesPipe

    abstract operator fun FilesPipe.plus(other: FilesPipe): FilesPipe

    abstract fun join(pipe0: FilesPipe, vararg pipeN: FilesPipe): FilesPipe

    abstract val apiVersion: Version
}