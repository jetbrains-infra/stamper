package ru.jetbrains.testenvrunner.service

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import org.springframework.stereotype.Service
import ru.jetbrains.testenvrunner.model.TerraformScript
import ru.jetbrains.testenvrunner.utils.ExecuteResult
import ru.jetbrains.testenvrunner.utils.ExecuteResultHandler
import ru.jetbrains.testenvrunner.utils.executeCommandAsync
import ru.jetbrains.testenvrunner.utils.executeCommandSync

/**
 * This service provide methods for communication with Terraform
 */
@Service
class TerraformExecutorService {
    /**
     * Plan and apply terraform script
     * @param script script that will be run
     */
    fun applyTerraformScript(script: TerraformScript): ExecuteResultHandler {
        val cmd = "terraform apply -no-color"
        return executeTerraformCommandAsync(cmd, script)
    }

    /**
     * Destroy the run infrastructure
     * @param script the script that will be stopped
     */
    fun destroyTerraformScript(script: TerraformScript): ExecuteResultHandler {
        val cmd = "terraform destroy -no-color -force"
        return executeTerraformCommandAsync(cmd, script)
    }

    /**
     * Check the script is run
     * @param script checked script
     * @return is run or no
     */
    fun isScriptRun(script: TerraformScript): Boolean {
        val result = executeCommandSync("terraform state list", directory = script.absolutePath)
        val msg: String = "No state file was found"
        if (result.exception != null && !result.output.contains(msg)) throw result.exception
        return !result.output.isEmpty() && !result.output.contains(msg)
    }

    /**
     * Get status of Terraform script
     * @param script checked script
     * @return status result
     */
    fun getStatus(script: TerraformScript): ExecuteResult {
        val result = executeCommandSync("terraform show -no-color", directory = script.absolutePath)
        if (result.exception != null) throw result.exception
        return result
    }

    /**
     * Get link how to run stack
     * This link should be formed in terraform output
     */
    fun getRunLink(script: TerraformScript): String {
        val result = executeCommandSync("terraform output -no-color -json", directory = script.absolutePath)
        if (result.output.contains("The state file either has no outputs defined")) return ""
        if (result.exception != null) throw result.exception
        val json: JsonObject = Parser().parse(StringBuilder(result.output)) as JsonObject
        return (json["link"] as JsonObject)["value"] as String? ?: ""
    }

    private fun executeTerraformCommandAsync(cmd: String, script: TerraformScript): ExecuteResultHandler {
        return executeCommandAsync(cmd, directory = script.absolutePath)
    }
}