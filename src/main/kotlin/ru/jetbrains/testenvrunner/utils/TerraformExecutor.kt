package ru.jetbrains.testenvrunner.utils

import org.springframework.stereotype.Service
import ru.jetbrains.testenvrunner.model.ExecutionCommand
import ru.jetbrains.testenvrunner.model.ExecutionResult
import ru.jetbrains.testenvrunner.model.TerraformScript

@Service
class TerraformExecutor constructor(val bashExecutor: BashExecutor) {
    /**
     * Plan and apply terraform script
     * @param script scrpit that will be run
     */
    fun executeTerraformScript(script: TerraformScript): ExecutionResult {
        return bashExecutor.executeCommand(ExecutionCommand("terraform apply -no-color"), directory = script.absolutePath)
    }

    /**
     * Destroy the run infrastructure
     * @param script the script that will be stopped
     */
    fun destroyTerraformScript(script: TerraformScript): ExecutionResult {
        return bashExecutor.executeCommand(ExecutionCommand("terraform destroy -no-color -force ${script.absolutePath}"), directory = script.absolutePath)
    }

    /**
     * Check the script is run
     * @param script checked script
     * @return is run or no
     */
    fun isScriptRun(script: TerraformScript): Boolean {
        val result = bashExecutor.executeCommand(ExecutionCommand("terraform show -no-color"), directory = script.absolutePath)
        if (result.exitValue != 0) throw Exception("error during check of script state")
        return result.output != "\n"
    }
}