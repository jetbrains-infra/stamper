package ru.jetbrains.testenvrunner.utils

import org.apache.commons.exec.*
import org.apache.commons.exec.environment.EnvironmentUtils
import ru.jetbrains.testenvrunner.model.ExecuteOperation
import ru.jetbrains.testenvrunner.model.ExecuteResult
import ru.jetbrains.testenvrunner.model.ExecuteResultHandler
import ru.jetbrains.testenvrunner.model.OperationStatus
import java.io.File

fun MutableList<String>.asString() = this.joinToString(separator = "\n") { it }.removeSuffix("\n")


val DEFAULT_WAITING_TIME: Long = 360

/**
 * Execute command asynchronously
 * @param executeOperation - operation that is performed
 * @param timeout - timeout that restricts the time for executing of the command
 * @return  handler for executing result
 *
 */
fun executeCommandAsync(executeOperation: ExecuteOperation,
                        timeout: Long = DEFAULT_WAITING_TIME): ExecuteResultHandler {
    val cmdLine = CommandLine.parse(executeOperation.command)
    val watchdog = ExecuteWatchdog((timeout * 1000))
    val executor = DefaultExecutor()
    if (!executeOperation.directory.isEmpty()) {
        executor.workingDirectory = File(executeOperation.directory)
    }

    val resultHandler = ExecuteResultHandler(executeOperation)
    executor.watchdog = watchdog
    executor.streamHandler = PumpStreamHandler(object : LogOutputStream() {

        override fun processLine(line: String, level: Int) {
            resultHandler.add(line)
        }
    })
    executor.execute(cmdLine, EnvironmentUtils.getProcEnvironment(), resultHandler)
    executeOperation.status = OperationStatus.RUNNING
    return resultHandler
}

/**
 * Execute command synchronously
 * @param executeOperation - operation that is performed
 * @param timeout - timeout that restricts the time for executing of the command
 * @return result of executing
 *
 */
fun executeCommandSync(executeOperation: ExecuteOperation, timeout: Long = DEFAULT_WAITING_TIME): ExecuteResult {
    val resultHandler = executeCommandAsync(executeOperation, timeout)
    resultHandler.waitFor()
    return executeOperation.executeResult
}