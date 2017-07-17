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
        model.addAttribute("runscripts", scriptRepository.getAllRun())
        return "index"
    }

    @RequestMapping("/result_terraform", method = arrayOf(RequestMethod.POST), params = arrayOf("action", "script-name"))
    fun doExecResultTerraformPost(model: Model, req: HttpServletRequest): String {
        val scriptName = req.getParameter("script-name")
        val action = req.getParameter("action")
        val terraformScript = scriptRepository.get(scriptName)

        val result = when (action) {
            "run" -> terraformExecutor.executeTerraformScript(terraformScript)
            "destroy" -> terraformExecutor.destroyTerraformScript(terraformScript)
            else -> throw Exception("Illegal action parameter")
        }
        model.addAttribute("result", result)
        return "result"
    }
}