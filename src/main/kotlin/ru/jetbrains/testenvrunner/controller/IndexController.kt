package ru.jetbrains.testenvrunner.controller

import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import ru.jetbrains.testenvrunner.exception.DeleteBeforeDestroyException
import ru.jetbrains.testenvrunner.repository.TemplateRepository
import ru.jetbrains.testenvrunner.service.DockerHubService
import ru.jetbrains.testenvrunner.service.StackService
import ru.jetbrains.testenvrunner.service.UserService
import javax.servlet.http.HttpServletRequest

@Controller
@RequestMapping("/")
class IndexController constructor(
        val userService: UserService,
        val stackService: StackService,
        val templateRepository: TemplateRepository,
        val dockerHubService: DockerHubService) {

    @RequestMapping(method = arrayOf(RequestMethod.GET))
    fun indexGet(model: Model, auth: OAuth2Authentication?): String {
        val user = userService.getUserByAuth(auth)
        model.addAttribute("user", user)
        model.addAttribute("templates", templateRepository.getAll())
        model.addAttribute("stacks", stackService.getAllStacks())
        return "index"
    }

    @RequestMapping(value = "/result_terraform", method = arrayOf(RequestMethod.POST), params = arrayOf("action=run"))
    fun runStack(model: Model, req: HttpServletRequest, auth: OAuth2Authentication): String {
        val templateName = req.getParameter("script_name")
        val stackName = req.getParameter("stack_name")

        val excludeParams = setOf("action", "script_name", "stack_name")
        val parameterMap = req.parameterMap.filter {
            !excludeParams.contains(it.key)
        }.map { it.key to it.value[0] }.toMap()

        val user = userService.getUserByAuth(auth)

        val operationId = stackService.runStack(templateName, stackName, parameterMap, user)
        model.addAttribute("handlerId", operationId)
        model.addAttribute("command", "apply")
        return "async"
    }

    @RequestMapping(value = "/result_terraform", method = arrayOf(RequestMethod.POST),
            params = arrayOf("action=destroy", "script-name"))
    fun destroyStack(model: Model, req: HttpServletRequest): String {
        val stackName = req.getParameter("script-name")
        val operationId = stackService.destroyStack(stackName)
        model.addAttribute("handlerId", operationId)
        model.addAttribute("command", "destroy")
        return "async"
    }

    @RequestMapping(value = "/delete_stack", method = arrayOf(RequestMethod.POST))
    fun deleteStack(model: Model, @RequestParam(value = "stack_name") stackName: String,
                    redirectAttrs: RedirectAttributes): String {
        try {
            stackService.deleteStack(stackName)
            redirectAttrs.addFlashAttribute("msg", "The stack is successfully deleted")
        } catch (e: DeleteBeforeDestroyException) {
            redirectAttrs.addFlashAttribute("msg_error",
                    "The stack '${e.stack.name}' cannot be deleted. Destroy the stack  and try to delete the stack again.")
        }
        return "redirect:/#"
    }


    @RequestMapping(value = "/run_param", method = arrayOf(RequestMethod.POST),
            params = arrayOf("action=run", "script-name"))
    fun openScriptRunForm(model: Model, @RequestParam(value = "script-name") templateName: String): String {
        val terraformScript = templateRepository.get(templateName)
        dockerHubService.fillAvailableDockerTags(terraformScript)
        model.addAttribute("script", terraformScript)
        return "run_param"
    }

    @RequestMapping(value = "/script/{id}", method = arrayOf(RequestMethod.GET))
    fun openRunningScriptForm(model: Model, @PathVariable(value = "id") stackName: String): String {
        val stack = stackService.getStack(stackName) ?: throw Exception("Stack is not found!")
        model.addAttribute("stack", stack)
        model.addAttribute("link", stackService.getStackRunLink(stack))
        model.addAttribute("operations", stackService.getComplectedStackOperations(stack))
        return "running_script"
    }

    @RequestMapping(value = "/stack/{id}/prolong", method = arrayOf(RequestMethod.GET))
    fun prolongStackExpireDate(model: Model, @PathVariable(value = "id") stackName: String,
                               redirectAttrs: RedirectAttributes): String {
        val stack = stackService.getStack(stackName) ?: throw Exception("Stack does not exists")
        stackService.prolongExpireDate(stack)
        model.addAttribute("msg", "The stack expire date is successfully prolonged")
        redirectAttrs.addFlashAttribute("msg", "The stack expire date is successfully prolonged")
        return "redirect:/script/$stackName"
    }
}