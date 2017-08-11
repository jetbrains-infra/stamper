package ru.jetbrains.testenvrunner.service

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import org.springframework.stereotype.Service
import ru.jetbrains.testenvrunner.model.ExecuteResult
import ru.jetbrains.testenvrunner.model.TerraformScript
import ru.jetbrains.testenvrunner.utils.executeCommandAsync
import ru.jetbrains.testenvrunner.utils.executeCommandSync

/**
 * This service provide methods for communication with Terraform
 */
@Service
class TerraformExecutorService(val operationService: OperationService) {
    /**
     * Plan and apply terraform script
     * @param script script that will be run
     */
    fun applyTerraformScript(script: TerraformScript): String {
        val cmd = "terraform apply -no-color"
        return executeTerraformCommandAsync(cmd, script)
    }

    /**
     * Destroy the run infrastructure
     * @param script the script that will be stopped
     */
    fun destroyTerraformScript(script: TerraformScript): String {
        val cmd = "terraform destroy -no-color -force"
        return executeTerraformCommandAsync(cmd, script)
    }

    /**
     * Check the script is run
     * @param script checked script
     * @return is run or no
     */
    fun isScriptRun(script: TerraformScript): Boolean {
        val result = executeTerraformCommandSync("terraform state list", script)
        val msg: String = "No state file was found"
        if (result.exception != null && !result.output.contains(msg)) {
            println(result.exception!!.message)
            result.exception!!.printStackTrace()
        }
        return !result.output.isEmpty() && !result.output.contains(msg)
    }

    /**
     * Get status of Terraform script
     * @param script checked script
     * @return status result
     */
    fun getStatus(script: TerraformScript): ExecuteResult {
        val result = executeTerraformCommandSync("terraform show -no-color", script)
        if (result.exception != null) {
            println(result.exception!!.message)
            result.exception!!.printStackTrace()
        }
        return result
    }

    /**
     * Get link how to run stack
     * This link should be formed in terraform output
     */
    fun getRunLink(script: TerraformScript): String {
        val result = executeTerraformCommandSync("terraform output -no-color -json", script)
        if (result.output.contains("The state file either has no outputs defined")) return ""
        if (result.exception != null) {
            println(result.exception!!.message)
            result.exception!!.printStackTrace()
            return ""
        }
        val json: JsonObject = Parser().parse(StringBuilder(result.output)) as JsonObject
        return (json["link"] as JsonObject)["value"] as String? ?: ""
    }

    private fun executeTerraformCommandAsync(cmd: String, script: TerraformScript): String {
        val operation = operationService.create(cmd, script.absolutePath)
        executeCommandAsync(operation)
        return operation.id
    }

    private fun executeTerraformCommandSync(cmd: String, script: TerraformScript): ExecuteResult {
        val operation = operationService.create(cmd, script.absolutePath, keepInSystem = false)
        return executeCommandSync(operation)
    }
}