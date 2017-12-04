package ru.jetbrains.testenvrunner.controller

import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import ru.jetbrains.testenvrunner.repository.TemplateRepository
import ru.jetbrains.testenvrunner.service.DockerService
import ru.jetbrains.testenvrunner.service.StackInfoService
import ru.jetbrains.testenvrunner.service.StackService
import ru.jetbrains.testenvrunner.service.UserService
import javax.servlet.http.HttpServletRequest

@Controller
@RequestMapping("/")
class IndexController constructor(
        val userService: UserService,
        val stackService: StackService,
        val templateRepository: TemplateRepository,
        val dockerService: DockerService,
        val stackInfoService: StackInfoService) {

    @RequestMapping(method = arrayOf(RequestMethod.GET))
    fun indexGet(model: Model, auth: OAuth2Authentication?): String {
        val user = userService.getUserByAuth(auth)
        model.addAttribute("user", user)
        model.addAttribute("templates", templateRepository.getAll())
        model.addAttribute("stacks", stackService.getAllExistStacks())
        return "redirect:http://localhost:3000/"
    }

    @RequestMapping(value = "/react", method = arrayOf(RequestMethod.GET))
    fun indexReactGet(model: Model): String {
        val bundleJavaScript = false
        model.addAttribute("bundleJavaScript", bundleJavaScript)
        return "react"
    }

    @RequestMapping(value = "/result_terraform", method = arrayOf(RequestMethod.POST), params = arrayOf("action=run"))
    fun runStack(model: Model, req: HttpServletRequest, auth: OAuth2Authentication?): String {
        val templateName = req.getParameter("script_name")
        val stackName = req.getParameter("name")

        val excludeParams = setOf("action", "script_name")
        val parameterMap = req.parameterMap.filter {
            !excludeParams.contains(it.key)
        }.map { it.key to it.value[0] }.toMap()

        val user = userService.getUserByAuth(auth)

        stackService.runStack(templateName, stackName, parameterMap, user)
        return "redirect:/script/$stackName"
    }



    @RequestMapping(value = "/run_param", method = arrayOf(RequestMethod.POST),
            params = arrayOf("action=run", "script-name"))
    fun openScriptRunForm(model: Model, @RequestParam(value = "script-name") templateName: String): String {
        val terraformScript = templateRepository.get(templateName)
        dockerService.fillAvailableDockerTags(terraformScript)
        model.addAttribute("script", terraformScript)
        return "run_param"
    }

    @RequestMapping(value = "/script/{id}", method = arrayOf(RequestMethod.GET))
    fun openStackCard(model: Model, @PathVariable(value = "id") stackName: String): String {
        val stack = stackService.getStack(stackName) ?: throw Exception("Stack is not found!")
        model.addAttribute("stack", stack)
        return "stack_card"
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

    @RequestMapping(value = "/stack/{id}/apply", method = arrayOf(RequestMethod.POST))
    fun applyStack(model: Model, @PathVariable(value = "id") stackName: String,
                   redirectAttrs: RedirectAttributes): String {
        stackService.reapplyStack(stackName)
        redirectAttrs.addFlashAttribute("msg", "try to reapply stack...")
        return "redirect:/script/$stackName"
    }
}