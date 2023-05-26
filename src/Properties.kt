package io.github.totwoqan

import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1

interface TaskConfigProperty<C : AbstractTaskConfig, T> {
    operator fun provideDelegate(owner: C, property: KProperty<*>): TaskConfigProperty<C, T>

    operator fun getValue(owner: C, property: KProperty<*>): T

    operator fun setValue(owner: C, property: KProperty<*>, newValue: T)
}
