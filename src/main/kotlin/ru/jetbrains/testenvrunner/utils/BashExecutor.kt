package ru.jetbrains.testenvrunner.utils

import mu.KotlinLogging
import ru.jetbrains.testenvrunner.model.ExecutionCommand
import ru.jetbrains.testenvrunner.model.ExecutionResult
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

class BashExecutor {
    companion object {
        private val LOG = KotlinLogging.logger {}
        /** Messages */
        val MSG_TIMEOUT_ERROR_BASH = "Execution time of Bash command \"%s\" exceeded the waiting time. "
        val MSG_ERROR_BASH = "Unexpected error during bash script execution. "
        val SUCCESS_BASH = "Bash script has been executed."
        val DEFAULT_WAITING_TIME: Long = 50

        /**
         * Running bash script
         * @param command - a command that will be executed
         * @param envParams - environment parameters
         * @param directory - a directory when the script will be executed, WARNING, you can work with files just in this directory
         * @param waitingTime - the script execution time limit
         * @return the result of bash script executing
         */
        fun executeCommand(command: ExecutionCommand, envParams: Map<String, String> = emptyMap(), directory: String = "", waitingTime: Long = DEFAULT_WAITING_TIME): ExecutionResult {
            val pb: ProcessBuilder = ProcessBuilder(command.command.split(" "))
            pb.redirectErrorStream(true)
            if (!directory.isEmpty()) {
                pb.directory(File(directory))
            }

            var p: Process? = null
            val result = try {
                //set addition environment parameters
                val env = pb.environment()
                env.putAll(envParams)

                p = pb.redirectOutput(ProcessBuilder.Redirect.PIPE)
                        .redirectError(ProcessBuilder.Redirect.PIPE)
                        .start()

                //throw exception if the time limit has been exceeded
                if (!p.waitFor(waitingTime, TimeUnit.SECONDS)) {
                    throw InterruptedException(MSG_TIMEOUT_ERROR_BASH.format(command.command))
                }
                val output = p.inputStream.bufferedReader().readText()

                LOG.info(SUCCESS_BASH + command.command)
                ExecutionResult(output, p.exitValue())
            } catch (e: InterruptedException) {
                LOG.error { e.message }
                ExecutionResult(e.message ?: MSG_ERROR_BASH)
            } catch (e: IOException) {
                LOG.error { "$e.message + \n + ${e.stackTrace}" }
                ExecutionResult(e.message ?: MSG_ERROR_BASH)
            } finally {
                if (p != null) {
                    p.destroyForcibly()
                }
            }
            return result
        }
    }
}