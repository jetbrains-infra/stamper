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
        model.addAttribute("runscripts", scriptRepository.getAllRunning())
        return "index"
    }

    @RequestMapping(value = "/result_terraform", method = arrayOf(RequestMethod.POST), params = arrayOf("action=run"))
    fun runScript(model: Model, req: HttpServletRequest): String {
        val scriptName = req.getParameter("script-name")
        val terraformScript = scriptRepository.get(scriptName)

        val parameterMap = req.parameterMap.filter { it.key != "action" && it.key != "script-name" }.map { it.key to it.value[0] }.toMap()
        scriptRepository.setParamValue(scriptName, parameterMap)
        val result = terraformExecutor.executeTerraformScript(terraformScript)
        model.addAttribute("result", result)
        return "result"
    }

    @RequestMapping(value = "/run_param", method = arrayOf(RequestMethod.POST), params = arrayOf("action=run", "script-name"))
    fun openScriptRunForm(model: Model, req: HttpServletRequest): String {
        val scriptName = req.getParameter("script-name")
        val terraformScript = scriptRepository.get(scriptName)
        model.addAttribute("script", terraformScript)
        return "run_param"
    }

    @RequestMapping(value = "/result_terraform", method = arrayOf(RequestMethod.POST), params = arrayOf("action=destroy", "script-name"))
    fun destroyScript(model: Model, req: HttpServletRequest): String {
        val scriptName = req.getParameter("script-name")
        val terraformScript = scriptRepository.get(scriptName)
        val result = terraformExecutor.destroyTerraformScript(terraformScript)
        model.addAttribute("result", result)
        return "result"
    }
}