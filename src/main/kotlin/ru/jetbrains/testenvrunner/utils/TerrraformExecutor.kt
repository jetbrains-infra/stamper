package ru.jetbrains.testenvrunner.utils

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import ru.jetbrains.testenvrunner.constants.SystemConstants
import ru.jetbrains.testenvrunner.model.ExecutionCommand
import ru.jetbrains.testenvrunner.model.ExecutionResult
import java.io.File

@Component
class TerrraformExecutor {
    @Value("\${script.terraform}")
    private lateinit var terraformRunScript: String

    /**
     * Plan and apply terraform script
     * @param script scrpit that will be runned
     */
    fun executeTerraformScript(script: File): ExecutionResult {
        return BashExecutor.executeCommand(ExecutionCommand("${SystemConstants.TERRAFORM_RUNNER} ${script.absolutePath}"))
    }
}