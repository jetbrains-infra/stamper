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
class ScriptRepository constructor(@Value("\${datadir}") val scriptFolder: String, val terraformExecutor: TerraformExecutor) {
    //consts
    val MSG_DIR_DOES_NOT_EXIST = ("The script %s does not exist in the system")

    fun getAll(): List<TerraformScript> {
        val directories = File(scriptFolder).listFiles(FileFilter { it.isDirectory })
        val scripts = directories.map { TerraformScript(it, getParam(it.name)) }
        return scripts.toList()
    }

    fun getAllRunning(): List<TerraformScript> {
        val scripts = getAll()

        return scripts.filter { terraformExecutor.isScriptRun(it) }
    }

    fun get(name: String): TerraformScript {
        val script = File("$scriptFolder$name")
        if (!script.exists())
            throw IOException(MSG_DIR_DOES_NOT_EXIST.format(name))
        return TerraformScript(script, getParam(name))
    }

    fun setParamValue(name: String, params: Map<String, Any>) {
        val json = JsonObject(params)
        val fileParams = File("$scriptFolder$name/terraform.tfvars.json")
        fileParams.createNewFile()
        fileParams.writeText(json.toJsonString(true))
    }

    private fun getParam(name: String): Map<String, Any?> {
        val paramsFile = File("$scriptFolder$name/variables.tf.json")
        if (!paramsFile.exists())
            return emptyMap()
        val parser: Parser = Parser()
        val json: JsonObject = parser.parse(paramsFile.absolutePath) as JsonObject
        val parameterMap = json["variable"] as JsonObject
        return parameterMap.map
    }
}
