package io.github.totwoqan

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

    fun <T : Task, C : AbstractTaskConfig<T>> create(configClass: KClass<*>, initializer: C.() -> Unit): T
    fun <T : Task, C : AbstractTaskConfig<T>> creating(configClass: KClass<*>, initializer: C.() -> Unit): CreatedTaskDelegateFactory<T>
}

interface CreatedTaskDelegateFactory<T : Task> {
    operator fun provideDelegate(owner: Any?, property: KProperty<*>): ReadOnlyProperty<*, T>
}

inline fun <T : Task, reified C : AbstractTaskConfig<T>> TasksScope.create(noinline initializer: C.() -> Unit): T =
    this.create(C::class, initializer)

inline fun <T : Task, reified C : AbstractTaskConfig<T>> TasksScope.creating(noinline initializer: C.() -> Unit): CreatedTaskDelegateFactory<T> =
    this.creating(C::class, initializer)