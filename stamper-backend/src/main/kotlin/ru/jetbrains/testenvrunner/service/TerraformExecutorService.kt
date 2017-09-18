package ru.jetbrains.testenvrunner.service

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import mu.KotlinLogging
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
                               @Value("\${terraform_image}") val terraformImage: String,
                               @Value("\${stack_volume}") val stackVolume: String?,
                               @Value("\${terraform_commands}") scriptFolderRelative: String,
                               @Value("\${stacks}") stackFolderRelative: String) {

    private val logger = KotlinLogging.logger {}
    val scriptFolder: String = File(scriptFolderRelative).canonicalPath
    val stackFolder: String = File(stackFolderRelative).canonicalPath

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
     * Get status of Terraform script
     * @param script checked script
     * @return status result
     */
    fun getStatus(script: TerraformScript): ExecuteResult {
        val result = executeTerraformCommandSync("sh $scriptFolder/show.sh ${script.name}", script)
        logExceptions("get status", result)
        return result
    }

    /**
     * Get link how to run stack
     * This link should be formed in terraform output
     */
    fun getOutputValues(script: TerraformScript): Map<String, String> {
        val result = executeTerraformCommandSync("sh $scriptFolder/output.sh ${script.name}", script)
        if (result.output.contains("The state file either has no outputs defined")) return emptyMap()
        logExceptions("get output", result)
        if (result.exception != null)
            return emptyMap()
        val json: JsonObject = Parser().parse(StringBuilder(result.output)) as JsonObject
        return json.map { (k, v) -> k to (v as JsonObject)["value"] as String }.toMap()
    }

    private fun executeTerraformCommandAsync(cmd: String, script: TerraformScript, title: String = cmd,
                                             handler: TerraformResultHandler? = null): String {
        val fullCmd = "$cmd  $terraformImage $stackFolder $stackVolume"
        val operation = operationService.create(fullCmd, script.absolutePath, title = title)
        logger.debug { "Start async executing operation ${operation.id} with command $fullCmd" }
        executeCommandAsync(operation, additionalHandler = handler)
        return operation.id
    }

    private fun executeTerraformCommandSync(cmd: String, script: TerraformScript): ExecuteResult {
        val fullCmd = "$cmd  $terraformImage $stackFolder $stackVolume"
        val operation = operationService.create(fullCmd, script.absolutePath, keepInSystem = false)
        logger.debug { "Start async executing operation ${operation.id} with command $fullCmd" }
        return executeCommandSync(operation)
    }

    private fun logExceptions(msg: String, result: ExecuteResult) {
        if (result.exception != null) {
            logger.error { "Exception during '$msg'\n Exception: ${result.exception}\nOutput: ${result.output}" }
        }
    }

}