package ru.jetbrains.testenvrunner.service

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ru.jetbrains.testenvrunner.model.ExecuteResult
import ru.jetbrains.testenvrunner.model.TerraformScript
import ru.jetbrains.testenvrunner.utils.executeCommandAsync
import ru.jetbrains.testenvrunner.utils.executeCommandSync
import java.io.File

/**
 * This service provide methods for communication with Terraform
 */
@Service
class TerraformExecutorService(val operationService: OperationService,
                               @Value("\${terraform_commands}") scriptFolderRelative: String) {

    val scriptFolder: String = File(scriptFolderRelative).canonicalPath

    /**
     * Plan and apply terraform script
     * @param script script that will be run
     */
    fun applyTerraformScript(script: TerraformScript, handler: TerraformResultHandler?): String {
        val cmd = "sh $scriptFolder/apply.sh ${script.name}"
        return executeTerraformCommandAsync(cmd, script, title = "terraform apply", handler = handler)
    }

    /**
     * Destroy the run infrastructure
     * @param script the script that will be stopped
     */
    fun destroyTerraformScript(script: TerraformScript, handler: TerraformResultHandler?): String {
        val cmd = "sh $scriptFolder/destroy.sh ${script.name}"
        return executeTerraformCommandAsync(cmd, script, "terraform destroy", handler = handler)
    }

    /**
     * Check the script is run
     * @param script checked script
     * @return is run or no
     */
    fun isScriptRun(script: TerraformScript): Boolean {
        val result = executeTerraformCommandSync("terraform state list", script)
        val msgStateFile = "No state file was found"
        val msgEnv = "Environment \"${script.name}\" doesn't exist!"
        if (result.exception != null && !result.output.contains(msgStateFile) && !result.output.contains(msgEnv)) {
            println(result.exception!!)
            result.exception!!
        }
        return !result.output.isEmpty() && !result.output.contains(msgStateFile) && !result.output.contains(msgEnv)
    }

    /**
     * Get status of Terraform script
     * @param script checked script
     * @return status result
     */
    fun getStatus(script: TerraformScript): ExecuteResult {
        val result = executeTerraformCommandSync("sh $scriptFolder/show.sh ${script.name}", script)
        if (result.exception != null) {
            println(result.exception!!)
            result.exception!!
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
            println(result.exception!!)
            result.exception!!
            return ""
        }
        val json: JsonObject = Parser().parse(StringBuilder(result.output)) as JsonObject
        return (json["link"] as JsonObject)["value"] as String? ?: ""
    }

    private fun executeTerraformCommandAsync(cmd: String, script: TerraformScript, title: String = cmd,
                                             handler: TerraformResultHandler? = null): String {
        val operation = operationService.create(cmd, script.absolutePath, title = title)
        executeCommandAsync(operation, additionalHandler = handler)
        return operation.id
    }

    private fun executeTerraformCommandSync(cmd: String, script: TerraformScript): ExecuteResult {
        val operation = operationService.create(cmd, script.absolutePath, keepInSystem = false)
        return executeCommandSync(operation)
    }
}