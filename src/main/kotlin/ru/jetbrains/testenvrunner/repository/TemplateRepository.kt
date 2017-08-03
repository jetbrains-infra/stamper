package ru.jetbrains.testenvrunner.repository

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import ru.jetbrains.testenvrunner.model.TerraformScript
import java.io.File
import java.io.FileFilter

@Repository
class TemplateRepository constructor(@Value("\${templates}") val scriptFolder: String) {
    //consts
    val MSG_DIR_DOES_NOT_EXIST = "The script %s does not exist in the system"

    fun getAll(): List<TerraformScript> {
        val directories = File(scriptFolder).listFiles(FileFilter { it.isDirectory })
        val scripts = directories.map { TerraformScript(it, getParam(it.name)) }
        return scripts.toList()
    }

    fun get(name: String): TerraformScript {
        val script = File("$scriptFolder/$name")
        if (!script.exists())
            throw Exception(MSG_DIR_DOES_NOT_EXIST.format(name))
        return TerraformScript(script, getParam(name))
    }

    private fun getParam(name: String): Map<String, Any?> {
        val paramsFile = File("$scriptFolder/$name/variables.tf.json")
        if (!paramsFile.exists())
            return emptyMap()
        val parser: Parser = Parser()
        val json: JsonObject = parser.parse(paramsFile.absolutePath) as JsonObject
        val parameterMap = json["variable"] as JsonObject
        return parameterMap.map
    }
}
