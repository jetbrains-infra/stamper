package ru.jetbrains.testenvrunner.repository

import org.junit.After
import org.junit.Before
import org.junit.Test
import ru.jetbrains.testenvrunner.model.TerraformScript
import java.io.File
import java.io.IOException
import javax.inject.Inject
import kotlin.test.assertFailsWith

class StackRepositoryTest : ScriptTest() {
    @Inject
    private lateinit var stackRepository: StackRepository

    @Before
    fun setUp() = removeAllData()

    @After
    fun tearDown() = removeAllData()

    @Test
    fun getAllStacksList() {
        assertEquals("there are not all tests", 0, stackRepository.getAll().size.toLong())

        val scripts = listOf(TerraformScript(File("${stacksFolder}/addAll1")), TerraformScript(File("${stacksFolder}/addAll2")))
        scripts.forEach({ addFakeStack(it.name) })

        val actualScripts = stackRepository.getAll()
        assertTrue("The getAll function return incorrect list of scripts", scripts.containsAll(actualScripts) && actualScripts.containsAll(scripts))
    }

    @Test
    fun getNonexistentStackTest() {
        assertFailsWith(IOException::class) {
            stackRepository.get("nonexistent")
        }
    }

    @Test
    fun getStackTest() {
        val script = TerraformScript(File("${stacksFolder}/add"))
        addFakeStack(script.name)
        assertEquals("The gotten script is not the same with added", script, stackRepository.get(script.name))
    }

    @Test
    fun createStackTest() {
        val params = mapOf("version" to mapOf("default" to "latest"), "name" to mapOf("default" to "fake"))
        val paramsValues = mapOf("version" to "5.5", "name" to "fake-name")
        val stackName = "stack"
        val template = addFakeTemplate("template", params)

        val createdStack = stackRepository.create(stackName, template, paramsValues)

        assertFileExists(createdStack.absolutePath + "/variables.tf.json")
        assertJsonFile(createdStack.absolutePath + "/terraform.tfvars.json", paramsValues)
    }

}