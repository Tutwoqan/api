package io.github.totwoqan


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
    protected abstract fun run()
}