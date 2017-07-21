package ru.jetbrains.testenvrunner.utils

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
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
    fun applyTerraformScript(script: TerraformScript): ExecutionResult {
        val cmd = "terraform apply -no-color"
        return executeTerraformCommand(cmd, script)
    }

    /**
     * Destroy the run infrastructure
     * @param script the script that will be stopped
     */
    fun destroyTerraformScript(script: TerraformScript): ExecutionResult {
        val cmd = "terraform destroy -no-color -force"
        return executeTerraformCommand(cmd, script)
    }

    /**
     * Check the script is run
     * @param script checked script
     * @return is run or no
     */
    fun isScriptRun(script: TerraformScript): Boolean {
        val result = bashExecutor.executeCommand(ExecutionCommand("terraform state list"), directory = script.absolutePath)
        val msg: String = "No state file was found"
        if (result.exitValue != 0 && !result.output.contains(msg)) throw Exception("error during check of script state\n ${result.output}")
        return !result.output.isEmpty() && !result.output.contains(msg)
    }

    fun getLink(script: TerraformScript): String {
        val result = bashExecutor.executeCommand(ExecutionCommand("terraform output -no-color -json"), directory = script.absolutePath)
        if (result.exitValue != 0) return ""
        val parser: Parser = Parser()
        val json: JsonObject = parser.parse(StringBuilder(result.output)) as JsonObject
        return ((json["link"] as JsonObject)["value"] as String?) ?: ""
    }

    private fun executeTerraformCommand(cmd: String, script: TerraformScript): ExecutionResult {
        val result = bashExecutor.executeCommand(ExecutionCommand(cmd), directory = script.absolutePath)
        if (result.exitValue != 0) throw TerraformExecutorException(result, cmd)
        return result
    }
}