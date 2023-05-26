package io.github.totwoqan

/**
 * Optional annotation for [TaskMetaFactory] and for `.kts` files.
 * If the maximum supported api version less than specified, build will be aborted.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FILE, AnnotationTarget.CLASS)
annotation class MinimumApiVersionRequired(val version: String)
