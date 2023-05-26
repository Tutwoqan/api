package io.github.totwoqan

import java.io.File
import kotlin.reflect.KClass

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

    /**
     * Logger for package which contains common global state for different tasks.
     *
     * @param global if global state also shared between project instances.
     */
    fun loggerFor(pkg: String, global: Boolean = false)

    /**
     * Logger for task with global state.
     *
     * @param global if global state shared between project instances.
     */
    fun loggerFor(meta: KClass<out TaskMetaFactory>, global: Boolean = false)

    /**
     * Task property factory. Properties are bound to owner [task config][AbstractTaskConfig] instance.
     * Before invoking [TaskMeta.createTask] will be checked, that all properties of specified instance are initialized.
     * Returned object can be updated only once.
     *
     * @throws IllegalStateException if function called outside [TaskMeta.createConfig] call (or it's subcalls).
     */
    fun <C : AbstractTaskConfig, T> taskConfigProperty(): TaskConfigProperty<C, T>

    /**
     * Task property factory. Properties are bound to owner [task config][AbstractTaskConfig] instance.
     * Property with default values wouldn't be checked because already initialized,
     * but it is anyway writable one time more.
     *
     * @throws IllegalStateException if function called outside [TaskMeta.createConfig] call (or it's subcalls).
     */
    fun <C : AbstractTaskConfig, T> taskConfigProperty(default: T): TaskConfigProperty<C, T>
}