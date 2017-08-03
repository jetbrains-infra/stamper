package ru.jetbrains.testenvrunner.utils

import org.apache.commons.exec.ExecuteException
import org.junit.Test
import ru.jetbrains.testenvrunner.repository.ScriptTest

class BashExecuteTest : ScriptTest() {

    @Test
    fun runEchoHelloSyncTest() {
        val command = "echo hello world"
        assertEquals("The result of bash script is not correct", ExecuteResult("hello world", 0),
                executeCommandSync(command))
    }

    @Test
    fun runEchoHelloAsyncTest() {
        val command = "echo hello world"
        val handler = executeCommandAsync(command)
        handler.waitFor()
        val actual = handler.executeResultParticle
        assertEquals("The result of bash script is not correct", ExecuteResultParticle("hello world", true), actual)
    }

    @Test
    fun runTimeExceedScriptAsyncTest() {
        val command = "ping google.ru"
        val handler = executeCommandAsync(command, timeout = 1)
        handler.waitFor()
        val actual = handler.executeResultParticle
        val exception = actual.exception

        assertTimeoutException(exception)
    }

    @Test
    fun runTimeExceedScriptSyncTest() {
        val command = "ping google.ru"
        val actual = executeCommandSync(command, timeout = 1)
        val exception = actual.exception
        assertTimeoutException(exception)
    }

    @Test
    fun runScriptInDirTest() {
        cleanTempDir()
        val dir = addTempDir("temp")
        val command = "pwd"
        val actual = executeCommandSync(command, directory = dir.canonicalPath)
        assertEquals("The result of bash script is not correct", ExecuteResult(dir.canonicalPath, 0), actual)

        cleanTempDir()
    }

    @Test
    fun runNonexistentScriptTest() {
        val command = "nonexistentProgram"
        val actual = executeCommandSync(command)
        val exception = actual.exception
        assertNotNull("There is not an exception", exception)

        assertTrue("There another exception ", exception!!.message!!.contains("No such file or directory"))
    }

    private fun assertTimeoutException(exception: ExecuteException?) {
        val TERMINATED_BY_SIGTERM_EXIT_CODE = 143
        assertNotNull("There is not a timeoutException exception", exception)
        assertEquals("There another exit code", TERMINATED_BY_SIGTERM_EXIT_CODE, exception!!.exitValue)
    }

}
