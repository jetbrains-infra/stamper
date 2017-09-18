package ru.jetbrains.testenvrunner.repository

import org.junit.After
import org.junit.Before
import org.junit.Test
import ru.jetbrains.testenvrunner.model.TerraformScript
import ru.jetbrains.testenvrunner.model.TerraformScriptParams
import java.io.File
import javax.inject.Inject
import kotlin.test.assertFailsWith

class StackDirectoryRepositoryTest : ScriptTest() {
    @Inject
    private lateinit var stackDirectoryRepository: StackDirectoryRepository

    @Before
    fun setUp() = removeAllData()

    @After
    fun tearDown() = removeAllData()

    @Test
    fun getAllStacksList() {
        assertEquals("there are not all tests", 0, stackDirectoryRepository.getAll().size.toLong())

        val scripts = listOf(TerraformScript(File("$stacksFolder/addAll1"), emptyTerraformScriptParams()),
                TerraformScript(File("$stacksFolder/addAll2"), emptyTerraformScriptParams()))
        scripts.forEach({ addFakeStack(it.name) })

        val actualScripts = stackDirectoryRepository.getAll()
        assertTrue("The getAll function return incorrect list of scriptFolder",
                scripts.containsAll(actualScripts) && actualScripts.containsAll(scripts))
    }

    @Test
    fun getNonexistentStackTest() {
        assertFailsWith(Exception::class) {
            stackDirectoryRepository.get("nonexistent")
        }
    }

    @Test
    fun getStackTest() {
        val script = TerraformScript(File("$stacksFolder/add"), TerraformScriptParams())
        addFakeStack(script.name)
        assertEquals("The gotten script is not the same with added", script, stackDirectoryRepository.get(script.name))
    }

    @Test
    fun createStackTest() {
        val params = mapOf("version" to mapOf("default" to "latest"), "name" to mapOf("default" to "fake"))
        val paramsValues = mapOf("version" to "5.5", "name" to "fake-name")
        val stackName = "stack"
        val template = addFakeTemplate("template", params)

        val createdStack = stackDirectoryRepository.create(stackName, template, paramsValues)

        assertFileExists(createdStack.absolutePath + "/variables.tf.json")
        assertJsonFile(createdStack.absolutePath + "/terraform.tfvars.json", paramsValues)
    }

}