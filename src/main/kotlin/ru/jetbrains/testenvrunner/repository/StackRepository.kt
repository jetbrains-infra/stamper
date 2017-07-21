package ru.jetbrains.testenvrunner.repository

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import ru.jetbrains.testenvrunner.model.TerraformScript
import ru.jetbrains.testenvrunner.utils.TerraformExecutor
import java.io.File
import java.io.FileFilter
import java.io.IOException

@Repository
class StackRepository constructor(@Value("\${stacks}") val stackFolder: String, val terraformExecutor: TerraformExecutor) {
    //consts
    val MSG_DIR_DOES_NOT_EXIST = ("The script %s does not exist in the system")

    /**
     * Get all running stacks
     */
    fun getAll(): List<TerraformScript> {
        val directories = File(stackFolder).listFiles(FileFilter { it.isDirectory })
        val scripts = directories.map { TerraformScript(it, getParam(it.name)) }
        return scripts.toList()
    }

    /**
     * Create stack that will be run
     * @param name - name of stack (it must be unique)
     * @param template - template that will be run
     * @param paramValues - running params of the script with values
     */
    fun create(name: String, template: TerraformScript, paramValues: Map<String, Any>): TerraformScript {
        val dir = File("$stackFolder/$name")
        if (!dir.mkdir()) throw IOException("The directory already exists")
        template.scriptDir.listFiles().forEach { it.copyRecursively(File("${dir.absolutePath}/${it.name}")) }
        setParamValue(name, paramValues)
        return TerraformScript(dir)
    }

    /**
     * Remove stack
     * @param name - name of stack
     */
    fun remove(name: String) {
        val dir = get(name).scriptDir
        dir.deleteRecursively()
    }

    fun get(name: String): TerraformScript {
        val script = File("$stackFolder/$name")
        if (!script.exists())
            throw IOException(MSG_DIR_DOES_NOT_EXIST.format(name))
        return TerraformScript(script, getParam(name))
    }

    fun setParamValue(name: String, params: Map<String, Any>) {
        val json = JsonObject(params)
        val fileParams = File("$stackFolder/$name/terraform.tfvars.json")
        fileParams.createNewFile()
        fileParams.writeText(json.toJsonString(true))
    }

    private fun getParam(name: String): Map<String, Any?> {
        val paramsFile = File("$stackFolder$name/variables.tf.json")
        if (!paramsFile.exists())
            return emptyMap()
        val parser: Parser = Parser()
        val json: JsonObject = parser.parse(paramsFile.absolutePath) as JsonObject
        val parameterMap = json["variable"] as JsonObject
        return parameterMap.map
    }
}
