package io.github.totwoqan

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