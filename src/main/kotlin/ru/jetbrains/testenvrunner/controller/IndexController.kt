package ru.jetbrains.testenvrunner.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import ru.jetbrains.testenvrunner.model.ExecutionCommand
import ru.jetbrains.testenvrunner.utils.BashExecutor
import ru.jetbrains.testenvrunner.utils.TerrraformExecutor
import java.io.File
import javax.inject.Inject

@Controller
@RequestMapping("/")
class IndexController {
    @Inject
    lateinit var terraformExecutor: TerrraformExecutor

    @RequestMapping(method = arrayOf(RequestMethod.GET))
    fun doIndexGet(model: Model): String {
        model.addAttribute("command", ExecutionCommand())
        return "index"
    }

    @RequestMapping("/result_terraform", method = arrayOf(RequestMethod.POST))
    fun doExecResultTerraformPost(model: Model): String {
        val result = terraformExecutor.executeTerraformScript(File("hello-world-config.tfplan"))
        model.addAttribute("result", result)
        return "result"
    }
}