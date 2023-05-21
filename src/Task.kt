package io.github.totwoqan

import kotlin.reflect.KClass

interface TaskMetaFactory {
    fun createMeta(project: ProjectLowApi): TaskMeta<*>
}

interface TaskMeta<CONFIG : AbstractTaskConfig> {
    fun createConfig(): CONFIG

    fun createTask(configLogger: Logger, config: CONFIG): Task

    fun finalize()
}


/**
 * Annotation for inheritors of [AbstractTaskConfig] to resolve corresponding [TaskMeta] instance
 * (one build system instance - one call to [createMeta][TaskMetaFactory.createMeta] of each referenced [TaskMetaFactory]).
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class TaskMetaReference(val factory: KClass<out TaskMetaFactory>)


/**
 * Abstract configuration for all tasks. Instances of inheritors of this class are
 * created by [TaskMeta.createConfig] and then converted to [tasks][Task] by [TaskMeta.createTask].
 * All inheritors must have annotation [TaskMetaReference].
 */
abstract class AbstractTaskConfig {
    /**
     * @see Task.dependencies
     */
    val dependencies: Set<Task> = HashSet()


    fun dependsOn(task0: Task?, vararg taskN: Task?) {
        this.dependencies as MutableSet<Task>
        if (task0 != null)
            this.dependencies.add(task0)
        this.dependencies.addAll(taskN.filterNotNull())
    }
}

/**
 * Base class of all tasks implementations.
 */
abstract class Task(config: AbstractTaskConfig) {
    /**
     * Set of tasks that must be complete before running this task.
     */
    val dependencies: Set<Task> = config.dependencies.toHashSet() // read-only copy

    /**
     * Tasks are not data objects, so equation should be done only by reference.
     */
    final override fun equals(other: Any?): Boolean = this === other

    /**
     * Tasks are not data objects, so hash must be distinct for distinct tasks ([JVM guaranteed][Object.hashCode]).
     */
    final override fun hashCode(): Int = super.hashCode()

    /**
     * Resets this task to uncompleted state.
     *
     * Must be not accessible at compile time, will be invoked via reflection.
     */
    protected abstract fun reset()

    /**
     * Main task's function.
     *
     * Must be not accessible at compile time, will be invoked via reflection.
     */
    protected abstract fun run(buildLogger: Logger)
}
