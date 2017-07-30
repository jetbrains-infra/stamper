package ru.jetbrains.testenvrunner.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import ru.jetbrains.testenvrunner.repository.StackFilesRepository
import ru.jetbrains.testenvrunner.repository.StackRepository
import ru.jetbrains.testenvrunner.repository.TemplateRepository
import ru.jetbrains.testenvrunner.service.StackService
import ru.jetbrains.testenvrunner.service.UserService
import ru.jetbrains.testenvrunner.utils.TerraformExecutor
import javax.servlet.http.HttpServletRequest

@Controller
@RequestMapping("/")
class IndexController constructor(
        val userService: UserService,
        val stackService: StackService,
        val terraformExecutor: TerraformExecutor,
        val templateRepository: TemplateRepository,
        val stackFilesRepository: StackFilesRepository) {

    @Autowired
    private lateinit var repositoryStack: StackRepository

    @RequestMapping(method = arrayOf(RequestMethod.GET))
    fun indexGet(model: Model, auth: OAuth2Authentication?): String {
        val user = userService.getUserByAuth(auth)
        model.addAttribute("user", user)
        model.addAttribute("templates", templateRepository.getAll())
        model.addAttribute("stacks", stackFilesRepository.getAll())
        return "index"
    }

    @RequestMapping(value = "/result_terraform", method = arrayOf(RequestMethod.POST), params = arrayOf("action=run"))
    fun runStack(model: Model, req: HttpServletRequest, auth: OAuth2Authentication): String {
        val templateName = req.getParameter("script-name")
        val stackName = req.getParameter("stack-name")

        val excludeParams = setOf("action", "script-name", "stack-name")
        val parameterMap = req.parameterMap.filter { !excludeParams.contains(it.key) }.map { it.key to it.value[0] }.toMap()

        val user = userService.getUserByAuth(auth)

        val result = stackService.runStack(templateName, stackName, parameterMap, user)
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
        val stackFile = stackFilesRepository.get(stackName)
        val stack = repositoryStack.findByName(stackName) ?: throw Exception("Stack is not found!")
        model.addAttribute("stack", stack)
        model.addAttribute("script", stackFile)
        model.addAttribute("link", terraformExecutor.getLink(stackFile))
        return "running_script"
    }

    @RequestMapping(value = "/result_terraform", method = arrayOf(RequestMethod.POST), params = arrayOf("action=destroy", "script-name"))
    fun destroyScript(model: Model, req: HttpServletRequest): String {
        val stackName = req.getParameter("script-name")
        val result = stackService.destroyStack(stackName)
        model.addAttribute("result", result)
        return "result"
    }
}