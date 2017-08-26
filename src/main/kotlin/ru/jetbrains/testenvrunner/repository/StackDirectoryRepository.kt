package ru.jetbrains.testenvrunner.repository

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import ru.jetbrains.testenvrunner.model.TerraformScript
import ru.jetbrains.testenvrunner.model.TerraformScriptParams
import java.io.File
import java.io.IOException

@Repository
class StackDirectoryRepository constructor(@Value("\${stacks}") stackFolder: String) : ScriptRepository(stackFolder) {

    override fun get(name: String): TerraformScript {
        val script = super.get(name)
        val fileParams = File("$scriptFolder/$name/terraform.tfvars.json")
        val json: JsonObject = Parser().parse(fileParams.absolutePath) as JsonObject
        json.forEach { (k, v) ->
            run {
                val param = script.params.firstOrNull { it.name == k }
                if (param != null)
                    param.value = v.toString()
            }
        }
        return script
    }

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

    /**
     * Set values of params in tfvars.json file
     * @param name - name of stack
     * @param params - variables of the stack
     */
    fun setParamValue(name: String, params: Map<String, Any>) {
        val json = JsonObject(params)
        val fileParams = File("$scriptFolder/$name/terraform.tfvars.json")
        fileParams.createNewFile()
        fileParams.writeText(json.toJsonString(true))
    }
}
