package ru.jetbrains.testenvrunner.repository

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringRunner
import ru.jetbrains.testenvrunner.model.TerraformScript
import ru.jetbrains.testenvrunner.model.TerraformScriptParams
import java.io.File
import java.io.IOException

@RunWith(SpringRunner::class)
@TestPropertySource(locations = arrayOf("classpath:test.properties"))
@SpringBootTest
class ScriptTest : Assert() {
    @Value("\${stacks}")
    lateinit var stacksFolder: String

    @Value("\${templates}")
    lateinit var templateFolder: String

    @Value("\${temp}")
    lateinit var templFolder: String

    val MSG_DIR_IS_NOT_DELETED = ("The script %s does not exist in the system")

    @Before
    fun init() {
        File(templFolder).mkdirs()
        File(templateFolder).mkdirs()
        File(stacksFolder).mkdirs()
    }

    protected fun emptyTerraformScriptParams() = TerraformScriptParams()

    protected fun addTempDir(name: String): File {
        return addFakeScriptDir(name, templFolder)
    }

    protected fun cleanTempDir() {
        removeAllInDir(templFolder)
    }

    protected fun addFakeTemplate(name: String, params: Map<String, Any> = emptyMap()): TerraformScript {
        val script = TerraformScript(File("$templateFolder/$name"), emptyTerraformScriptParams())
        val dir = addFakeScriptDir(name, templateFolder)
        if (params.isEmpty()) return script

        val emptySript = File("$templateFolder/$name/main.tf")
        emptySript.createNewFile()
        val fileName = "variables.tf.json"
        val fullParam = mapOf("variable" to params)
        writeMapToJsonFile(dir, fileName, fullParam)
        return script
    }

    protected fun addFakeStack(name: String, params: Map<String, Any> = emptyMap(), paramsValues: Map<String, Any> = emptyMap()): TerraformScript {
        val script = TerraformScript(File("$stacksFolder/$name"), emptyTerraformScriptParams())
        val dir = addFakeScriptDir(name, stacksFolder)

        if (params.isEmpty()) return script
        val varFile = "variables.tf.json"
        val fullParam = mapOf("variable" to params)
        writeMapToJsonFile(dir, varFile, fullParam)

        if (paramsValues.isEmpty()) return script
        val varValuesFile = "terraform.tfvars.json"
        writeMapToJsonFile(dir, varValuesFile, paramsValues)
        return script
    }

    protected fun removeAllData() {
        removeAllInDir(templateFolder)
        removeAllInDir(stacksFolder)
    }

    protected fun assertJsonFile(filePath: String, actualMap: Map<String, Any>) {
        assertFileExists(filePath)
        val file = File(filePath)
        val parser = Parser()
        val json: JsonObject = parser.parse(file.absolutePath) as JsonObject
        assertEquals("The content of JSON file  ${file.name} is not the same with expected", actualMap, json.map)
    }

    protected fun assertFileExists(filePath: String) {
        val file = File(filePath)
        assertTrue("File ${file.name} does not exists", file.exists())
    }

    private fun addFakeScriptDir(name: String, folder: String): File {
        val dir = File("$folder/$name")
        if (dir.exists())
            throw IOException("The directory exists!")
        dir.mkdirs()
        return dir
    }

    private fun removeAllInDir(path: String) {
        val mainDir = File(path)
        mainDir.listFiles()
                .filterNot { it.deleteRecursively() }
                .forEach { throw IOException(MSG_DIR_IS_NOT_DELETED.format(it.name)) }
    }

    private fun writeMapToJsonFile(dir: File, fileName: String, params: Map<String, Any>) {
        val paramsFile = File("${dir.absolutePath}/$fileName")
        paramsFile.createNewFile()
        val json = JsonObject(params)
        paramsFile.writeText(json.toJsonString())
    }

    @Test
    fun fakeParentTest() = assertTrue(true)
}

