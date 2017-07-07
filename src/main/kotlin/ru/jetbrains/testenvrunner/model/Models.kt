package ru.jetbrains.testenvrunner.model

data class ExecutionCommand(var command: String = "")

data class ExecutionResult(val output: String = "", val exitValue: Int = -1)