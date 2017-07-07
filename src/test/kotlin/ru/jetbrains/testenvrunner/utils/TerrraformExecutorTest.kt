package ru.jetbrains.testenvrunner.utils

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringRunner
import java.io.File
import javax.inject.Inject

@RunWith(SpringRunner::class)
@SpringBootTest
@TestPropertySource(locations = arrayOf("classpath:test.properties"))
class TerrraformExecutorTest : Assert() {
    @Inject
    lateinit var terraformExecurtor: TerrraformExecutor

    @Value("\${script.helloworld}")
    lateinit var helloWorldScript: String

    @Test
    fun executeTerraformScriptSuccess() {
        val result = terraformExecurtor.executeTerraformScript(File(helloWorldScript))
        assertTrue("The terraform script run fail. Exit code: ${result.exitValue}", result.exitValue == 0)
    }
}