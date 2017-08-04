package ru.jetbrains.testenvrunner.repository

import com.beust.klaxon.JsonObject
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import ru.jetbrains.testenvrunner.model.TerraformScript
import ru.jetbrains.testenvrunner.model.TerraformScriptParams
import java.io.File
import java.io.IOException

@Repository
class StackFilesRepository constructor(@Value("\${stacks}") stackFolder: String) : ScriptRepository(stackFolder) {
    /**
     * Create stack that will be run
     * @param name - name of stack (it must be unique)
     * @param template - template that will be run
     * @param paramValues - running params of the script with values
     */
    fun create(name: String, template: TerraformScript, paramValues: Map<String, Any>): TerraformScript {
        val dir = File("$scriptFolder/$name")
        if (!dir.mkdir()) throw IOException("The directory already exists")
        template.scriptDir.listFiles().forEach { it.copyRecursively(File("${dir.absolutePath}/${it.name}")) }
        setParamValue(name, paramValues)
        return TerraformScript(dir, TerraformScriptParams())
    }

    /**
     * Remove stack
     * @param name - name of stack
     */
    fun remove(name: String) {
        val dir = get(name).scriptDir
        dir.deleteRecursively()
    }


    fun setParamValue(name: String, params: Map<String, Any>) {
        val json = JsonObject(params)
        val fileParams = File("$scriptFolder/$name/terraform.tfvars.json")
        fileParams.createNewFile()
        fileParams.writeText(json.toJsonString(true))
    }
}
