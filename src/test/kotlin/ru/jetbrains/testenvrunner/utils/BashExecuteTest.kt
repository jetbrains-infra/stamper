package ru.jetbrains.testenvrunner.utils

import org.apache.commons.exec.ExecuteException
import org.junit.Test
import ru.jetbrains.testenvrunner.model.ExecuteOperation
import ru.jetbrains.testenvrunner.model.ExecuteResult
import ru.jetbrains.testenvrunner.model.ExecuteResultParticle
import ru.jetbrains.testenvrunner.model.OperationStatus
import ru.jetbrains.testenvrunner.repository.ScriptTest

class BashExecuteTest : ScriptTest() {
    @Test
    fun runEchoHelloSyncTest() {
        val command = create("echo hello world")
        assertEquals("The result of bash script is not correct",
                ExecuteResult(mutableListOf("hello world"), 0, hasResult = true),
                executeCommandSync(command))
    }

    @Test
    fun runEchoHelloAsyncTest() {
        val command = create("echo hello world")
        val handler = executeCommandAsync(command)
        handler.waitFor()
        val actual = handler.operation.executeResult.getParticleResult(0)
        assertEquals("The result of bash script is not correct", ExecuteResultParticle("hello world", 1, true, null, 0),
                actual)
    }

    @Test
    fun runTimeExceedScriptAsyncTest() {
        val command = create("ping google.ru")
        val handler = executeCommandAsync(command, timeout = 1)
        handler.waitFor()
        val exception = command.executeResult.getParticleResult(0).exception
        assertEquals("The exception message is wrong", "Process exited with an error: 143 (Exit value: 143)", exception)
    }

    @Test
    fun runTimeExceedScriptSyncTest() {
        val command = create("ping google.ru")
        val actual = executeCommandSync(command, timeout = 1)
        val exception = actual.exception
        assertEquals("The exception message is wrong", "Process exited with an error: 143 (Exit value: 143)", exception)
    }

    @Test
    fun runScriptInDirTest() {
        cleanTempDir()
        val dir = addTempDir("temp")
        val command = create("pwd").copy(directory = dir.canonicalPath)
        val actual = executeCommandSync(command)
        assertEquals("The result of bash script is not correct",
                ExecuteResult(mutableListOf(dir.canonicalPath), 0, hasResult = true), actual)
        cleanTempDir()
    }

    @Test
    fun runNonexistentScriptTest() {
        val command = create("nonexistentProgram")
        val actual = executeCommandSync(command)
        val exception = actual.exception
        assertNotNull("There is not an exception", exception)

        assertTrue("There another exception ", exception!!.contains("No such file or directory"))
    }

    private fun assertTimeoutException(exception: ExecuteException?) {
        val TERMINATED_BY_SIGTERM_EXIT_CODE = 143
        assertNotNull("There is not a timeoutException exception", exception)
        assertEquals("There another exit code", TERMINATED_BY_SIGTERM_EXIT_CODE, exception!!.exitValue)
    }

    private fun create(command: String, directory: String = "", keepInSystem: Boolean = true): ExecuteOperation {
        val id = generateRandomWord()
        val executeOperation = ExecuteOperation(command, directory, ExecuteResult(), OperationStatus.CREATED, id,
                keepInSystem, "terraform apply", DateUtils().getCurrentDate())
        return executeOperation
    }

}
