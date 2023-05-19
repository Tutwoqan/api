package io.github.totwoqan

import java.io.File

/**
 * Connector between producer and consumers tasks.
 * Itself it is a lazy set that fills up as [producer tasks][FilesPipe.tasks] are completed
 * and cleared after building restart, so tasks should ***create*** pipe only once at creating and
 * ***send*** files to it again on each completing.
 * Accessing property [.files][FilesPipe.files] after clearing and before ***all*** [producer tasks][FilesPipe.tasks]
 * are complete will cause [IllegalStateException].
 *
 * Third-party implementations (that are not part of implementation of build system) mustn't cache any files because
 * build system doesn't know that they must be cleared at restart.
 *
 * When [task config][AbstractTaskConfig] receives pipe, it must explicitly add all [producer tasks][FilesPipe.tasks]
 * of received pipe via [AbstractTaskConfig.dependsOn] to ensure that all files will be produced before it will be requsted.
 */
interface FilesPipe {
    /**
     * Set of tasks that must be completed before [reading files][FilesPipe.files] from this pipe.
     *
     * Must be known before creating pipe object.
     */
    val tasks: Set<Task>

    /**
     * Produced files. Can be read only after all [producer tasks][FilesPipe.tasks] are completed and before clearing.
     *
     * Filled by bound [FilesPipeWriter] or wrapped pipes.
     *
     * @throws IllegalStateException if any of [producer tasks][FilesPipe.tasks] are not complete.
     */
    val files: Set<File>
}