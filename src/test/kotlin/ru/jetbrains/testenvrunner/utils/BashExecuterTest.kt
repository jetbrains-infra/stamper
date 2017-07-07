package ru.jetbrains.testenvrunner.utils

import org.junit.Assert
import org.junit.Test
import ru.jetbrains.testenvrunner.model.ExecutionCommand

class BashExecuterTest : Assert() {
    private val MSG_OUTPUT = "The wrong result of executed bash command"
    private val MSG_EXIT_CODE = "The wrong exitValue of executed bash command"

    @Test
    fun runEchoHelloTest() {
        val value = "hello, world!"
        val result = BashExecutor.executeCommand(ExecutionCommand("echo $value"))
        assertEquals(MSG_OUTPUT, value + "\n", result.output)
        assertTrue(MSG_EXIT_CODE, result.exitValue == 0)
    }

    @Test
    fun runExceedTimeLimitCommandTest() {
        val value = "ping google.com"

        val result = BashExecutor.executeCommand(ExecutionCommand(value), waitingTime = 1)
        assertEquals(MSG_OUTPUT, BashExecutor.MSG_TIMEOUT_ERROR_BASH.format(value), result.output)
        assertTrue(MSG_EXIT_CODE, result.exitValue != 0)
    }

    @Test
    fun runNonexistentCommandTest() {
        val value = "abrakadabra"
        val result = BashExecutor.executeCommand(ExecutionCommand(value), waitingTime = 1)
        assertEquals(MSG_OUTPUT, "Cannot run program \"$value\": error=2, No such file or directory", result.output)
        assertTrue(MSG_EXIT_CODE, result.exitValue != 0)
    }
}
