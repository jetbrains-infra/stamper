package ru.jetbrains.testenvrunner.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import ru.jetbrains.testenvrunner.repository.ScriptRepository
import ru.jetbrains.testenvrunner.utils.TerraformExecutor
import javax.servlet.http.HttpServletRequest

@Controller
@RequestMapping("/")
class IndexController constructor(val terraformExecutor: TerraformExecutor, val scriptRepository: ScriptRepository) {
    @RequestMapping(method = arrayOf(RequestMethod.GET))
    fun doIndexGet(model: Model): String {
        model.addAttribute("scripts", scriptRepository.getAll())
        return "index"
    }

    @RequestMapping("/result_terraform", method = arrayOf(RequestMethod.POST), params = arrayOf("run-script"))
    fun doExecResultTerraformPost(model: Model, req: HttpServletRequest): String {
        val scriptName = req.getParameter("run-script")
        val terraformScript = scriptRepository.get(scriptName)
        val result = terraformExecutor.executeTerraformScript(terraformScript)
        model.addAttribute("result", result)
        return "result"
    }
}