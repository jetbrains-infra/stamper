package ru.jetbrains.testenvrunner.utils

import ru.jetbrains.testenvrunner.model.ExecutionResult

private val MSG_TERRAFORM_COMMAND_EXECUTION_ERROR = "The terraform command cannot be applied.\n Command: %s\n Output: %s"

data class TerraformExecutorException(val executionResult: ExecutionResult, private val cmd: String) : Exception() {
    override val message = MSG_TERRAFORM_COMMAND_EXECUTION_ERROR.format(cmd, executionResult.output)
}