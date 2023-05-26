package io.github.totwoqan

import io.github.totwoqan.stdtask.CopyFile
import io.github.totwoqan.stdtask.CopyFileTask
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

interface VariableScopes {
    val export: VariableScope
    val project: VariableScope
    val file: VariableScope
    fun createScope(name: String): VariableScope

    operator fun get(scopeName: String): VariableScope
}

interface VariableScope {
    operator fun get(name: String): Any?

    operator fun set(name: String, value: Any?)

    operator fun <T> provideDelegate(owner: Any?, property: KProperty<*>): ReadWriteProperty<*, T>

    fun delete(name: String)
}


interface TasksScopes {
    val export: VariableScope
    val project: VariableScope
    val file: VariableScope
    fun createScope(name: String): TasksScope

    operator fun get(scopeName: String): TasksScope
}

interface TasksScope {
    operator fun get(name: String): Any?

    operator fun set(name: String, value: Any?)

    operator fun <T : Task> provideDelegate(owner: Any?, property: KProperty<*>): ReadOnlyProperty<*, T>

    fun <M : TaskType<C, T>, C : AbstractTaskConfig, T : Task> create(taskType: M, initializer: C.() -> Unit): T
    fun <M : TaskType<C, T>, C : AbstractTaskConfig, T : Task> creating(taskType: M, initializer: C.() -> Unit): CreatedTaskDelegateFactory<T>
}

interface CreatedTaskDelegateFactory<T : Task> {
    operator fun provideDelegate(owner: Any?, property: KProperty<*>): ReadOnlyProperty<Any?, T>
}