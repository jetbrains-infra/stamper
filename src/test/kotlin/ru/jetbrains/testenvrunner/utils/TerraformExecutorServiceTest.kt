package ru.jetbrains.testenvrunner.utils

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringRunner
import ru.jetbrains.testenvrunner.model.TerraformScript
import ru.jetbrains.testenvrunner.repository.ScriptTest
import ru.jetbrains.testenvrunner.service.OperationService
import ru.jetbrains.testenvrunner.service.TerraformExecutorService
import ru.jetbrains.testenvrunner.service.TerraformResultHandler
import java.io.File
import javax.inject.Inject

@RunWith(SpringRunner::class)
@SpringBootTest
@TestPropertySource(locations = arrayOf("classpath:test.properties"))
class TerraformExecutorServiceTest : ScriptTest() {
    @Inject
    lateinit var terraformExecutorService: TerraformExecutorService

    @Inject
    lateinit var operationService: OperationService

    @Inject
    lateinit var terraformResulthandler: TerraformResultHandler

    @Value("\${script.helloworld}")
    lateinit var helloWorldScript: String

    @Test
    fun executeAndDestroyTerraformScriptSuccess() {
        val script = TerraformScript(File(helloWorldScript), emptyTerraformScriptParams())
        //check run
        val applyId = terraformExecutorService.applyTerraformScript(script, terraformResulthandler)
        waitFor(applyId)
        val runResult = operationService.get(applyId).executeResult
        assertEquals("The terraform script run fail. Exit code: ${runResult.exitCode}", 0, runResult.exitCode)
        //check that script state is run
        assertTrue("The terraform script state fail. Script is stopped", terraformExecutorService.isScriptRun(script))

        assertEquals("The link is not the same", "http://google.ru", terraformExecutorService.getRunLink(script))
        //check stop
        val destroyId = terraformExecutorService.destroyTerraformScript(script, terraformResulthandler)
        waitFor(destroyId)
        val destroyResult = operationService.get(applyId).executeResult
        assertEquals("The terraform script destroy fail. Exit code: ${destroyResult.exitCode}", 0,
                destroyResult.exitCode)
        //check that script is stopped
        assertFalse("The terraform script state fail. Script is run", terraformExecutorService.isScriptRun(script))
    }

    private fun waitFor(id: String) {
        while (!operationService.isCompleted(id)) {
            Thread.sleep(500)
        }
    }
}