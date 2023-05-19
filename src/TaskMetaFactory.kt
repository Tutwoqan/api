package io.github.totwoqan

/**
 *
 */
interface TaskMetaFactory {
    fun createMeta(project: ProjectLowApi): TaskMeta<*>
}