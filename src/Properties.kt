package io.github.totwoqan

import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1

private fun throwNotDelegatedYet(): Nothing =
    throw IllegalStateException("This object not delegated to any property yet")

private fun KProperty<*>?.checkIsDelegated(): KProperty<*> =
    this ?: throwNotDelegatedYet()

private fun throwAnotherProperty(): Nothing =
    throw IllegalArgumentException("This object delegated to another property")

private fun KProperty<*>.checkIsSame(property: KProperty<*>) {
    if (this !== property)
        throwAnotherProperty()
}

fun <R : Any, V> KProperty1<R, V>.getAssert(instance: R): V {
    @Suppress("UNCHECKED_CAST")
    val delegate = this.getDelegate(instance) as TaskConfigPropertySingleValue<R, V>
    return TaskConfigPropertySingleValue.assertGet(delegate)
}

class TaskConfigPropertySingleValue<R : Any, V> {

    private enum class State(
        @JvmField
        val isOverridable: Boolean,
        @JvmField
        val isAccessible: Boolean
    ) {
        UNSET(true, false),
        DEFAULT(true, true),
        SET(false, true)
    }

    private var owner: R? = null
    private var delegate: KProperty<*>? = null
    private var state: State
    private var value: V?

    constructor() {
        this.state = State.UNSET
        this.value = null
    }

    constructor(default: V) {
        this.state = State.DEFAULT
        this.value = default
    }

    operator fun provideDelegate(owner: R, property: KProperty<*>): TaskConfigPropertySingleValue<R, V> {
        if (this.delegate != null)
            throw IllegalStateException("This object can't be delegated to more than one property. Owner: ${this.delegate}")
        this.delegate = property
        this.owner = owner
        return this
    }

    operator fun getValue(owner: R, property: KProperty<*>): V {
        this.delegate.checkIsDelegated().checkIsSame(property)

        @Suppress("RemoveRedundantQualifierName")
        if (!this.state.isAccessible)
            TaskConfigPropertySingleValue.throwNotInitialized(property.name)

        @Suppress("UNCHECKED_CAST")
        return this.value as V
    }

    operator fun setValue(owner: R, property: KProperty<*>, newValue: V) {
        this.delegate.checkIsDelegated().checkIsSame(property)

        @Suppress("RemoveRedundantQualifierName")
        if (!this.state.isOverridable)
            TaskConfigPropertySingleValue.throwReinitialization(property.name)

        this.state = State.SET
        this.value = newValue
    }

    companion object {
        @JvmStatic
        fun throwNotInitialized(name: String): Nothing {
            throw IllegalStateException("Property `${name}` not initialized yet")
        }

        @JvmStatic
        fun throwReinitialization(name: String): Nothing {
            throw IllegalStateException("Property `${name}` already initialized and can't be overridden anymore")
        }

        @JvmStatic
        fun hasValue(property: TaskConfigPropertySingleValue<*, *>): Boolean = property.state.isAccessible

        @JvmStatic
        fun <R : Any, V> assertGet(property: TaskConfigPropertySingleValue<R, V>): V =
            property.getValue(property.owner!!, property.delegate!!) // todo
    }
}