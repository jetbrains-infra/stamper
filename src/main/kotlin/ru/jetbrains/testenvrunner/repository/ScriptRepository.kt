package ru.jetbrains.testenvrunner.repository

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import org.springframework.stereotype.Repository
import ru.jetbrains.testenvrunner.model.TerraformScript
import ru.jetbrains.testenvrunner.model.TerraformScriptParams
import ru.jetbrains.testenvrunner.model.createTerraformParam
import java.io.File
import java.io.FileFilter

@Repository
abstract class ScriptRepository constructor(val scriptFolder: String) {
    val MSG_DIR_DOES_NOT_EXIST = "The script %s does not exist in the system"

    /**
     * Get all scripts in folder
     * @return available scripts
     */
    fun getAll(): List<TerraformScript> {
        val directories = File(scriptFolder).listFiles(FileFilter { it.isDirectory })
        val scripts = directories.map { TerraformScript(it, getParam(it.name)) }
        return scripts.toList()
    }

    /**
     * Get script by name
     * @throws Exception if script does not exists
     * @return [TerraformScript] if it exists
     */
    fun get(name: String): TerraformScript {
        val script = File("$scriptFolder/$name")
        if (!script.exists())
            throw Exception(MSG_DIR_DOES_NOT_EXIST.format(name))
        return TerraformScript(script, getParam(name))
    }

    /**
     * Get script variables with values
     * @param name of script
     * @return the params of the script
     */
    protected fun getParam(name: String): TerraformScriptParams {
        val paramsFile = File("$scriptFolder/$name/variables.tf.json")
        if (!paramsFile.exists())
            return TerraformScriptParams()
        val parser: Parser = Parser()
        val json: JsonObject = parser.parse(paramsFile.absolutePath) as JsonObject
        val parameterMap = json["variable"] as JsonObject

        val terraformScriptParams = TerraformScriptParams()
        for ((k, v) in parameterMap) {
            val paramMap = (v as JsonObject).map
            val param = createTerraformParam(k, paramMap)
            terraformScriptParams.add(param)
        }
        return terraformScriptParams
    }
}
