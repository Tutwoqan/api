package io.github.totwoqan

import kotlin.reflect.KClass

/**
 * Annotation for inheritors of [AbstractTaskConfig] to resolve corresponding [TaskMeta] instance
 * (one build system instance - one call to [createMeta][TaskMetaFactory.createMeta] of each referenced [TaskMetaFactory]).
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class TaskMetaReference(val factory: KClass<out TaskMetaFactory>)