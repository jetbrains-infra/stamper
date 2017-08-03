package ru.jetbrains.testenvrunner.utils

import org.apache.commons.exec.*
import java.io.File
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

/**
 * Particular result of executing, during the process
 */
data class ExecuteResultParticle(val newText: String, val isEnd: Boolean, val exception: ExecuteException? = null)

/**
 * Full results of executing that can be gotten after end of executing the operation
 */
data class ExecuteResult(val output: String, val exitCode: Int, val exception: ExecuteException? = null)

/**
 * Executor result handler that get all information about executing
 */
open class ExecuteResultHandler(val resultHandler: DefaultExecuteResultHandler) {

    val id = generateRandomWord()
    val queue: BlockingQueue<String> = LinkedBlockingQueue()

    val output: MutableList<String> = mutableListOf()

    fun add(string: String) = queue.offer(string)

    val newOutput: MutableList<String>
        get() {
            val result = mutableListOf<String>()
            queue.drainTo(result)
            output.addAll(result)
            return result
        }

    val newOutputString: String
        get() {
            return newOutput.joinToString(separator = "\n") { it }.removeSuffix("\n")
        }

    val exception: ExecuteException?
        get() = try {
            resultHandler.exception
        } catch (e: IllegalStateException) {
            null
        }

    val executeResultParticle: ExecuteResultParticle
        get() = ExecuteResultParticle(newOutputString, isEnd(), exception = exception)

    val executionResult: ExecuteResult
        get() {
            resultHandler.waitFor()
            newOutput
            return ExecuteResult(output.joinToString(separator = "\n") { it }.removeSuffix("\n"),
                    resultHandler.exitValue, exception = exception)
        }

    fun waitFor() = resultHandler.waitFor()
    fun isEnd() = resultHandler.hasResult()
}

val DEFAULT_WAITING_TIME: Long = 360
/**
 * Execute command asynchronously
 * @param command - command that is performed
 * @param directory - directory, where the command is performed
 * @param timeout - timeout that restricts the time for executing of the command
 * @return  handler for executing result
 *
 */
fun executeCommandAsync(command: String, directory: String = "",
                        timeout: Long = DEFAULT_WAITING_TIME): ExecuteResultHandler {
    val cmdLine = CommandLine.parse(command)
    val resultHandler = DefaultExecuteResultHandler()

    val queue = ExecuteResultHandler(resultHandler)
    val watchdog = ExecuteWatchdog((timeout * 1000))
    val executor = DefaultExecutor()
    if (!directory.isEmpty()) {
        executor.workingDirectory = File(directory)
    }
    executor.watchdog = watchdog
    executor.streamHandler = PumpStreamHandler(object : LogOutputStream() {

        override fun processLine(line: String, level: Int) {
            queue.add(line)
        }
    })
    executor.execute(cmdLine, resultHandler)
    return queue
}

/**
 * Execute command synchronously
 * @param command - command that is performed
 * @param directory - directory, where the command is performed
 * @param timeout - timeout that restricts the time for executing of the command
 * @return result of executing
 *
 */
fun executeCommandSync(command: String, directory: String = "", timeout: Long = DEFAULT_WAITING_TIME): ExecuteResult {
    val handler = executeCommandAsync(command, directory, timeout)
    return handler.executionResult
}