package ru.jetbrains.testenvrunner.utils

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ru.jetbrains.testenvrunner.model.ExecutionCommand
import ru.jetbrains.testenvrunner.model.ExecutionResult
import ru.jetbrains.testenvrunner.model.TerraformScript

@Service
class TerraformExecutor constructor(val bashExecutor: BashExecutor, @Value("\${terraformrunner}") val terraformRunner: String) {
    /**
     * Plan and apply terraform script
     * @param script scrpit that will be runned
     */
    fun executeTerraformScript(script: TerraformScript): ExecutionResult {
        return bashExecutor.executeCommand(ExecutionCommand("$terraformRunner ${script.absolutePath}"))
    }
}