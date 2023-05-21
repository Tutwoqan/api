package io.github.totwoqan

import kotlin.reflect.KClass

interface TaskMeta<CONFIG : AbstractTaskConfig> {
    fun createConfig(): CONFIG

    fun createTask(configLogger: Logger, config: CONFIG): Task

    fun finalize()
}