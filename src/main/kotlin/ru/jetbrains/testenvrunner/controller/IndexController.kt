package ru.jetbrains.testenvrunner.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import ru.jetbrains.testenvrunner.repository.StackRepository
import ru.jetbrains.testenvrunner.repository.TemplateRepository
import ru.jetbrains.testenvrunner.utils.TerraformExecutor
import ru.jetbrains.testenvrunner.utils.TerraformExecutorException
import javax.servlet.http.HttpServletRequest

@Controller
@RequestMapping("/")
class IndexController constructor(val terraformExecutor: TerraformExecutor, val templateRepository: TemplateRepository, val stackRepository: StackRepository) {
    @RequestMapping(method = arrayOf(RequestMethod.GET))
    fun indexGet(model: Model): String {
        model.addAttribute("templates", templateRepository.getAll())
        model.addAttribute("stacks", stackRepository.getAll())
        return "index"
    }

    @RequestMapping(value = "/result_terraform", method = arrayOf(RequestMethod.POST), params = arrayOf("action=run"))
    fun runStack(model: Model, req: HttpServletRequest): String {
        val templateName = req.getParameter("script-name")
        val stackName = req.getParameter("stack-name")
        val templateScript = templateRepository.get(templateName)

        val excludeParams = setOf("action", "script-name", "stack-name")
        val parameterMap = req.parameterMap.filter { !excludeParams.contains(it.key) }.map { it.key to it.value[0] }.toMap()

        val stack = stackRepository.create(stackName, templateScript, parameterMap)
        val result = try {
            terraformExecutor.applyTerraformScript(stack)
        } catch (e: TerraformExecutorException) {
            stackRepository.remove(stackName)
            e.executionResult
        }
        model.addAttribute("result", result)
        return "result"
    }

    @RequestMapping(value = "/run_param", method = arrayOf(RequestMethod.POST), params = arrayOf("action=run", "script-name"))
    fun openScriptRunForm(model: Model, req: HttpServletRequest): String {
        val templateName = req.getParameter("script-name")
        val terraformScript = templateRepository.get(templateName)
        model.addAttribute("script", terraformScript)
        return "run_param"
    }

    @RequestMapping(value = "/script/{id}", method = arrayOf(RequestMethod.GET))
    fun openRunningScriptForm(model: Model, @PathVariable(value = "id") stackName: String, req: HttpServletRequest): String {
        val stack = stackRepository.get(stackName)
        model.addAttribute("script", stack)
        model.addAttribute("link", terraformExecutor.getLink(stack))
        return "running_script"
    }

    @RequestMapping(value = "/result_terraform", method = arrayOf(RequestMethod.POST), params = arrayOf("action=destroy", "script-name"))
    fun destroyScript(model: Model, req: HttpServletRequest): String {
        val stackName = req.getParameter("script-name")
        val stack = stackRepository.get(stackName)

        val result = try {
            val res = terraformExecutor.destroyTerraformScript(stack)
            stackRepository.remove(stackName)
            res
        } catch (e: TerraformExecutorException) {
            e.executionResult
        }
        model.addAttribute("result", result)
        return "result"
    }
}