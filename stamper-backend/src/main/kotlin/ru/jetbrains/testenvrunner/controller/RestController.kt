package ru.jetbrains.testenvrunner.controller

import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import ru.jetbrains.testenvrunner.exception.StackNotFoundException
import ru.jetbrains.testenvrunner.model.*
import ru.jetbrains.testenvrunner.repository.TemplateRepository
import ru.jetbrains.testenvrunner.service.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/api/")
class RestWebController constructor(val stackService: StackService,
                                    val operationService: OperationService,
                                    val stackInfoService: StackInfoService,
                                    val templateRepository: TemplateRepository,
                                    val dockerService: DockerService,
                                    val userService: UserService) {

    @RequestMapping(value = "/new-output", method = arrayOf(RequestMethod.GET))
    fun getOperationResult(@RequestParam("id") id: String, @RequestParam("start") start: Int): ExecuteResultParticle {
        val operation = operationService.get(id)
        return operation.executeResult.getParticleResult(start)
    }

    @RequestMapping(value = "/stack/{id}", method = arrayOf(RequestMethod.GET))
    @ResponseBody
    fun getStackById(@PathVariable(value = "id") stackName: String): Stack? {
        return stackService.getStack(stackName)
    }

    @RequestMapping(value = "/stack/{id}/status", method = arrayOf(RequestMethod.GET))
    @ResponseBody
    fun getStackStatus(@PathVariable(value = "id") stackName: String): OutputStatus {
        val stack = stackService.getStack(stackName) ?: throw StackNotFoundException()
        return OutputStatus(stack.status, stackInfoService.getTerraformStatus(stack),
                stackInfoService.getLastCommandId(stack))
    }

    @RequestMapping(value = "/stack/{id}/logs", method = arrayOf(RequestMethod.GET))
    @ResponseBody
    fun getStackLogs(@PathVariable(value = "id") stackName: String): List<ExecuteOperation> {
        val stack = stackService.getStack(stackName) ?: throw StackNotFoundException()
        return stackInfoService.getStackLogs(stack)
    }

    @RequestMapping(value = "/stack/{id}", method = arrayOf(RequestMethod.DELETE))
    @ResponseBody
    fun destroyStack(@PathVariable(value = "id") stackName: String, @RequestParam(value = "force") force: Boolean) {
        if (force) {
            stackInfoService.markStackDeleted(stackName)
            return
        }
        stackService.destroyStack(stackName)
    }

    @RequestMapping(value = "/templates/{id}", method = arrayOf(RequestMethod.GET))
    @ResponseBody
    fun getTemplate(@PathVariable(value = "id") name: String): TerraformScript {
        val template = templateRepository.get(name)
        dockerService.fillAvailableDockerTags(template)
        return template
    }

    @RequestMapping(value = "/templates", method = arrayOf(RequestMethod.GET))
    @ResponseBody
    fun getTemplates(): List<TerraformScript> {
        return templateRepository.getAll()
    }

    @RequestMapping(value = "/stacks", method = arrayOf(RequestMethod.GET))
    @ResponseBody
    fun getStacks(): List<Stack> {
        return stackService.getAllExistStacks()
    }

    @RequestMapping("/user")
    fun getAuthUser(auth: OAuth2Authentication?): User? {
        return userService.getUserByAuth(auth)
    }

    @RequestMapping(value = "/template/{id}", method = arrayOf(RequestMethod.POST))
    fun runStack(req: HttpServletRequest, @PathVariable(value = "id") templateName: String,
                 auth: OAuth2Authentication?) {
        val data = req.parameterMap.map { it.key to it.value.get(0) }.toMap()
        val stackName = data["name"] ?: throw Exception()
        val user = userService.getUserByAuth(auth)

        stackService.runStack(templateName, stackName, data, user)
    }

    @RequestMapping(value = "/stack/{id}/apply", method = arrayOf(RequestMethod.POST))
    fun applyStack(model: Model, @PathVariable(value = "id") stackName: String,
                   redirectAttrs: RedirectAttributes) = stackService.reapplyStack(stackName)

    @RequestMapping(value = "/log/{id}", method = arrayOf(RequestMethod.GET))
    @ResponseBody
    fun getLog(@PathVariable(value = "id") id: String): ExecuteOperation = operationService.get(id)
}

data class OutputStatus(val stackStatus: StackStatus, val output: String? = null, val commandId: String?)