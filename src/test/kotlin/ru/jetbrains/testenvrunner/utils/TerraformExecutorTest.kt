package ru.jetbrains.testenvrunner.utils

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringRunner
import ru.jetbrains.testenvrunner.model.TerraformScript
import java.io.File
import javax.inject.Inject

@RunWith(SpringRunner::class)
@SpringBootTest
@TestPropertySource(locations = arrayOf("classpath:test.properties"))
class TerraformExecutorTest : Assert() {
    @Inject
    lateinit var terraformExecurtor: TerraformExecutor

    @Value("\${script.helloworld}")
    lateinit var helloWorldScript: String

    @Test
    fun executeAndDestroyTerraformScriptSuccess() {
        val script = TerraformScript(File(helloWorldScript))
        //check run
        val resultRun = terraformExecurtor.executeTerraformScript(script)
        assertTrue("The terraform script run fail. Exit code: ${resultRun.exitValue}", resultRun.exitValue == 0)
        //check that script state is run
        assertTrue("The terraform script state fail. Script is stopped", terraformExecurtor.isScriptRun(script))
        //check stop
        val resultDestroy = terraformExecurtor.destroyTerraformScript(script)
        assertTrue("The terraform script destroy fail. Exit code: ${resultDestroy.exitValue}", resultDestroy.exitValue == 0)
        //check that script is stopped
        assertFalse("The terraform script state fail. Script is run", terraformExecurtor.isScriptRun(script))
    }
}