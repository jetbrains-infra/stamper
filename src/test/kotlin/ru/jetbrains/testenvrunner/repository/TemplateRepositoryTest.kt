package ru.jetbrains.testenvrunner.repository

import org.junit.After
import org.junit.Before
import org.junit.Test
import ru.jetbrains.testenvrunner.model.TerraformScript
import ru.jetbrains.testenvrunner.model.TerraformScriptParam
import java.io.File
import javax.inject.Inject
import kotlin.test.assertFailsWith

class TemplateRepositoryTest : ScriptTest() {
    @Inject
    private lateinit var templateRepository: TemplateRepository

    @Before
    fun setUp() = removeAllData()

    @After
    fun tearDown() = removeAllData()

    @Test
    fun getAllScriptsList() {
        assertEquals("there are not all tests", 0, templateRepository.getAll().size.toLong())

        val scripts = listOf(TerraformScript(File("$templateFolder/addAll1"), emptyTerraformScriptParams()),
                TerraformScript(File("$templateFolder/addAll2"), emptyTerraformScriptParams()))
        scripts.forEach { addFakeTemplate(it.name) }

        val actualScripts = templateRepository.getAll()
        assertTrue("The getAll function return incorrect list of scripts", scripts.containsAll(actualScripts) && actualScripts.containsAll(scripts))
    }

    @Test
    fun getScriptWithParamsTest() {
        val scriptName = "addparam"
        val scriptFake = TerraformScript(File("$templateFolder/$scriptName"), emptyTerraformScriptParams())

        val variables = mapOf("version" to mapOf("default" to "latest"))
        addFakeTemplate(scriptFake.name, variables)
        val script = templateRepository.get(scriptName)

        val expectedParams = emptyTerraformScriptParams()
        expectedParams.add(TerraformScriptParam("version", defaultValue = "latest"))
        assertEquals("Parameters are not the same", expectedParams, script.params)
    }

    @Test
    fun getScriptWithoutParamsTest() {
        val script = TerraformScript(File("$templateFolder/add"), emptyTerraformScriptParams())
        addFakeTemplate(script.name)
        assertEquals("The gotten script is not the same with added", script, templateRepository.get(script.name))
    }

    @Test
    fun getNonexistentScriptTest() {
        assertFailsWith(Exception::class) {
            templateRepository.get("nonexistent")
        }
    }
}