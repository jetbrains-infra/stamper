package ru.jetbrains.testenvrunner.utils

import org.junit.Assert
import org.junit.Test
import ru.jetbrains.testenvrunner.model.ExecutionCommand

class BashExecuterTest : Assert() {
    private val msg = "The wrong result of executed bash command"

    @Test
    fun runEchoHelloTest() {
        val value = "hello, world!"
        val result = BashExecutor.executeCommand(ExecutionCommand("echo $value"))
        assertEquals(msg, value + "\n", result.result)
    }

    @Test
    fun runExceedTimeLimitCommandTest() {
        val value = "ping google.com"

        val result = BashExecutor.executeCommand(ExecutionCommand(value), waitingTime = 1)
        assertEquals(msg, BashExecutor.MSG_TIMEOUT_ERROR_BASH.format(value), result.result)
    }

    @Test
    fun runNonexistentCommandTest() {
        val value = "abrakadabra"

        val result = BashExecutor.executeCommand(ExecutionCommand(value), waitingTime = 1)
        assertEquals(msg, "Cannot run program \"$value\": error=2, No such file or directory", result.result)
    }
}
