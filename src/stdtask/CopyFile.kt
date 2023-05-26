package io.github.totwoqan.stdtask

import io.github.totwoqan.AbstractTaskConfig
import io.github.totwoqan.FatalError
import io.github.totwoqan.FilesPipe
import io.github.totwoqan.FilesPipeWriter
import io.github.totwoqan.Logger
import io.github.totwoqan.ProjectLowApi
import io.github.totwoqan.Task
import io.github.totwoqan.TaskMeta
import io.github.totwoqan.TaskMetaFactory
import io.github.totwoqan.TaskMetaReference
import java.io.File
import java.nio.file.DirectoryNotEmptyException
import java.nio.file.FileAlreadyExistsException
import java.nio.file.Files.delete
import java.nio.file.NoSuchFileException


private object CopyFileTaskMetaFactory : TaskMetaFactory {
    override fun createMeta(project: ProjectLowApi): TaskMeta<*> =
        CopyFileTaskMeta(project)
}

private class CopyFileTaskMeta(private val project: ProjectLowApi) : TaskMeta<CopyFileConfig> {
    override fun createConfig(): CopyFileConfig =
        CopyFileConfig(this.project)

    override fun createTask(configLogger: Logger, config: CopyFileConfig): Task {
        return CopyFileTask(this.project, config)
    }

    override fun finalize() {}

}

@TaskMetaReference(CopyFileTaskMetaFactory::class)
class CopyFileConfig(project: ProjectLowApi) : AbstractTaskConfig() {
    var source: FilesPipe by project.taskConfigProperty()
    var destination: File by project.taskConfigProperty()
    var createMissingDirectories: Boolean by project.taskConfigProperty(true)
}

@Suppress("unused")
typealias CopyFile = CopyFileConfig

class CopyFileTask(project: ProjectLowApi, config: CopyFileConfig) : Task(config) {
    private val createMissingDirectories = config.createMissingDirectories
    private val sourcePipe = config.source
    private val destinationFile = config.destination
    private val destinationPipeWriter: FilesPipeWriter

    @Suppress("MemberVisibilityCanBePrivate")
    val copiedFiles: FilesPipe

    init {
        val (fp, fpw) = project.createPipe()
        this.destinationPipeWriter = fpw
        this.copiedFiles = fp
    }

    override fun reset() {}


    private fun unwrapSet(set: Set<File>, logger: Logger): File {
        when {
            set.size > 1  -> {
                logger.fatal {
                    text("Too many source files, can't copy them all into one file:")
                    newline()
                    text("May be you typo in task type (CopyFile -> CopyFiles)")
                    newline()
                    text("Source files:")
                    for (sf in set)
                        newline().indent().file(sf)
                }
                throw FatalError
            }

            set.isEmpty() -> {
                logger.fatal("Source file list is empty, nothing to copy")
                throw FatalError
            }
        }
        return set.first()
    }

    private fun cleanDirectory(logger: Logger) {
        for (file2remove in this.destinationFile.walkBottomUp()) {
            try {
                delete(file2remove.toPath())
            } catch (exc: Throwable) {
                when (exc) {
                    is DirectoryNotEmptyException, is NoSuchFileException -> {
                        logger.fatal {
                            text("Directory walk error, make sure that you don't change the directory to be deleted while task is running")
                            newline()
                            text("File: ").file(file2remove)
                            newline()
                            exception(exc)
                        }
                    }

                    else                                                  -> {
                        logger.fatal {
                            text("Uncaught exception while deleting directory content")
                            newline()
                            text("File: ").file(file2remove)
                            newline()
                            exception(exc)
                        }
                    }
                }
                throw FatalError
            }
            logger.trace {
                text("Deleted file: ")
                file(file2remove, this@CopyFileTask.destinationFile)
            }
        }
    }

    private fun copyDirectory(sourceDirectory: File, logger: Logger) {
        logger.debug("Source file is directory `${sourceDirectory.absolutePath}`")
        if (this.destinationFile.exists()) {
            if (this.destinationFile.isDirectory) {
                logger.info("Destination is directory, its content will be removed")
                this.cleanDirectory(logger)
                logger.debug("Destination directory cleared")
            } else {
                logger.fatal("Destination is not a directory, delete it before copying")
                throw FatalError
            }
        }

        for (file2copy in sourceDirectory.walkTopDown()) {
            val dst = this.destinationFile.resolve(file2copy.relativeTo(sourceDirectory))
            try {
                file2copy.copyTo(dst, overwrite = false)
            } catch (exc: FileAlreadyExistsException) {
                logger.fatal {
                    text("Destination file already exists, make sure that you don't change the destination directory while task is running")
                    newline()
                    text("File: ").file(dst)
                    newline()
                    exception(exc)
                }
                throw FatalError
            } catch (exc: NoSuchFileException) {
                logger.fatal {
                    text("Source file not found, make sure that you don't change the directory to be copied while task is running")
                    newline()
                    text("File: ").file(file2copy)
                    newline()
                    exception(exc)
                }
                throw FatalError
            } catch (exc: Throwable) {
                logger.fatal {
                    text("Uncaught exception while copying file")
                    newline()
                    text("File: ")
                    // those 2 files looks same, but actually references to different files
                    file(file2copy, sourceDirectory).text(" -> ").file(dst, this@CopyFileTask.destinationFile)
                    newline()
                    exception(exc)
                }
                throw FatalError
            }
            logger.trace {
                text("File copied: ")
                // those 2 files looks same, but actually references to different files
                file(file2copy, sourceDirectory).text(" -> ").file(dst, this@CopyFileTask.destinationFile)
            }
            this.destinationPipeWriter.add(dst)
        }
        logger.info("Directory copied successful")
    }

    private fun copyFile(sourceFile: File, logger: Logger) {
        if (this.destinationFile.exists() && !this.destinationFile.isFile) {
            logger.fatal("Destination is not a file, delete it before copying")
            throw FatalError
        }

        try {
            sourceFile.copyTo(this.destinationFile, overwrite = true)
        } catch (exc: NoSuchFileException) {
            logger.fatal {
                text("Source file not found")
                newline()
                text("File: ").file(sourceFile)
                newline()
                exception(exc)
            }
            throw FatalError
        } catch (exc: Throwable) {
            logger.fatal {
                text("Uncaught exception while copying file")
                newline()
                text("File: ").file(sourceFile)
                newline()
                exception(exc)
            }
            throw FatalError
        }
    }

    override fun run(buildLogger: Logger) {
        val sourceFile = this.unwrapSet(this.sourcePipe.files, buildLogger)
        if (!sourceFile.exists()) {
            buildLogger.fatal {
                text("Source file doesn't exists: ")
                file(sourceFile)
            }
            throw FatalError
        }

        if (sourceFile == this.destinationFile) {
            buildLogger.warning {
                text("Source and destination paths are same, nothing to do")
                newline()
                text("File: ")
                file(sourceFile)
            }
            return
        }

        when {
            sourceFile.isDirectory -> this.copyDirectory(sourceFile, buildLogger)
            sourceFile.isFile      -> this.copyFile(sourceFile, buildLogger)
            else                   -> buildLogger.fatal {
                text("Don't know how to copy source file: ")
                file(sourceFile)
                throw FatalError
            }
        }
    }
}