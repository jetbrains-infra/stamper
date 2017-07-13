package ru.jetbrains.testenvrunner.repository

import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringRunner
import ru.jetbrains.testenvrunner.model.TerraformScript
import java.io.File
import java.io.IOException
import javax.inject.Inject
import kotlin.test.assertFailsWith

@RunWith(SpringRunner::class)
@TestPropertySource(locations = arrayOf("classpath:test.properties"))
@SpringBootTest
class ScriptRepositoryTest : Assert() {
    @Before
    fun setUp() {
        removeAll()
    }

    @After
    fun tearDown() {
        removeAll()
    }

    @Test
    fun getAllScriptsList() {
        assertEquals("there are not all tests", 0, scriptRepository.getAll().size.toLong())

        val scripts = listOf(TerraformScript(File("${scriptFolder}addAll1")), TerraformScript(File("${scriptFolder}addAll2")))
        for (script in scripts) {
            addFake(script)
        }

        val actualScripts = scriptRepository.getAll()
        assertTrue("The getAll function return incorrect list of scripts", scripts.containsAll(actualScripts) && actualScripts.containsAll(scripts))
    }

    @Test
    fun AddAndGetScriptTest() {
        val script = TerraformScript(File("${scriptFolder}add"))
        addFake(script)
        assertEquals("The gotten script is not the same with added", script, scriptRepository.get(script.name))
    }

    @Test
    fun getNonexistentScriptTest() {
        assertFailsWith(IOException::class) {
            scriptRepository.get("nonexistent")
        }
    }

    @Inject
    private lateinit var scriptRepository: ScriptRepository

    @Value("\${datadir}")
    private lateinit var scriptFolder: String

    val MSG_DIR_IS_NOT_DELETED = ("The script %s does not exist in the system")
    private fun addFake(script: TerraformScript) {
        val dir = File(script.absolutePath)
        if (dir.exists())
            throw IOException("The directory exists!")
        dir.mkdirs()
    }

    private fun removeAll() {
        val mainDir = File(scriptFolder)
        for (dir in mainDir.listFiles()) {
            if (!dir.deleteRecursively()) {
                throw IOException(MSG_DIR_IS_NOT_DELETED.format(dir.name))
            }
        }
    }
}