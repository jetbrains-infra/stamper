package ru.jetbrains.testenvrunner.model

import org.apache.commons.exec.DefaultExecuteResultHandler
import org.apache.commons.exec.ExecuteException
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.PersistenceConstructor
import org.springframework.data.annotation.Transient
import ru.jetbrains.testenvrunner.service.OperationService
import ru.jetbrains.testenvrunner.utils.asString
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Particular result of executing, during the process
 */
data class ExecuteResultParticle(val newText: String, val last: Int, val isEnd: Boolean,
                                 val exception: ExecuteException?)

/**
 * Full results of executing that can be gotten after end of executing the operation
 */
data class ExecuteResult(val outputList: MutableList<String> = CopyOnWriteArrayList(),
                         var exitCode: Int? = null,
                         @Transient var exception: ExecuteException? = null,
                         var hasResult: Boolean = false) {

    val output get() = outputList.asString()

    fun getParticleResult(from: Int = 0): ExecuteResultParticle {
        val size = outputList.size
        val newOutput = outputList.subList(from, size).asString()
        return ExecuteResultParticle(newOutput, size, hasResult, exception)
    }
}

enum class OperationStatus {
    CREATED, RUNNING, SUCCESS, FAILED
}

data class ExecuteOperation @PersistenceConstructor constructor(val command: String,
                                                                val directory: String,
                                                                val executeResult: ExecuteResult,
                                                                var status: OperationStatus,
                                                                @Id val id: String,
                                                                val keepInSystem: Boolean)

/**
 * Executor result handler that get all information about executing
 */
class ExecuteResultHandler(val operation: ExecuteOperation) : DefaultExecuteResultHandler() {

    fun add(string: String) = operation.executeResult.outputList.add(string)

    override fun onProcessFailed(e: ExecuteException?) {
        super.onProcessFailed(e)
        updateOperationResult()
        OperationService.operationService.fail(operation)

    }

    override fun onProcessComplete(exitValue: Int) {
        super.onProcessComplete(exitValue)
        updateOperationResult()
        OperationService.operationService.success(operation)
    }

    override fun getException(): ExecuteException? {
        return try {
            super.getException()
        } catch (e: IllegalStateException) {
            null
        }
    }

    private fun updateOperationResult() {
        with(operation.executeResult) {
            exception = getException()
            exitCode = exitValue
            hasResult = hasResult()
        }
    }
}
