package ru.jetbrains.testenvrunner.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import ru.jetbrains.testenvrunner.exception.CreateStackWithExistNameException
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

    @RequestMapping(value = ["/new-output"], method = [(RequestMethod.GET)])
    fun getOperationResult(@RequestParam("id") id: String, @RequestParam("start") start: Int): ExecuteResultParticle {
        val operation = operationService.get(id)
        return operation.executeResult.getParticleResult(start)
    }

    @RequestMapping(value = ["/stack/{id}"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun getStackById(@PathVariable(value = "id") stackName: String): Stack? {
        return stackService.getStack(stackName)
    }

    @RequestMapping(value = ["/stack/{id}/status"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun getStackStatus(@PathVariable(value = "id") stackName: String): Map<String, String> {
        val stack = stackService.getStack(stackName) ?: throw StackNotFoundException()
        return mapOf("stateInfo" to stackInfoService.getTerraformStatus(stack))
    }

    @RequestMapping(value = ["/stack/{id}/logs"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun getStackLogs(@PathVariable(value = "id") stackName: String): List<ExecuteOperation> {
        val stack = stackService.getStack(stackName) ?: throw StackNotFoundException()
        return stackInfoService.getStackLogs(stack)
    }

    @RequestMapping(value = ["/stack/{id}"], method = [(RequestMethod.DELETE)])
    @ResponseBody
    fun destroyStack(@PathVariable(value = "id") stackName: String, @RequestParam(value = "force") force: Boolean) {
        if (force) {
            stackInfoService.markStackDeleted(stackName)
            return
        }
        stackService.destroyStack(stackName)
    }

    @RequestMapping(value = ["/templates/{id}"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun getTemplate(@PathVariable(value = "id") name: String): TerraformScript {
        val template = templateRepository.get(name)
        dockerService.fillAvailableDockerTags(template)
        return template
    }

    @RequestMapping(value = ["/templates"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun getTemplates(): List<TerraformScript> {
        return templateRepository.getAll()
    }

    @RequestMapping(value = ["/stacks"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun getStacks(): List<Stack> {
        return stackService.getAllExistStacks()
    }

    @RequestMapping("/user")
    fun getAuthUser(auth: OAuth2Authentication?): User? {
        return userService.getUserByAuth(auth)
    }

    @RequestMapping(value = ["/template/{id}"], method = [(RequestMethod.POST)])
    fun runStack(req: HttpServletRequest, @PathVariable(value = "id") templateName: String,
                 auth: OAuth2Authentication?): ResponseEntity<Map<String, String>>? {
        val data = req.parameterMap.map { it.key to it.value[0] }.toMap()
        val stackName = data["name"] ?: throw Exception()
        val user = userService.getUserByAuth(auth) ?: return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                mapOf("msg" to "You should be authenticated in the system"))

        return try {
            stackService.runStack(templateName, stackName, data, user)
            ResponseEntity.ok(null)
        } catch (e: CreateStackWithExistNameException) {
            ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    mapOf("msg" to e.message))
        }
    }

    @RequestMapping(value = ["/stack/{id}/apply"], method = [(RequestMethod.POST)])
    fun applyStack(model: Model, @PathVariable(value = "id") stackName: String,
                   redirectAttrs: RedirectAttributes) = stackService.reapplyStack(stackName)

    @RequestMapping(value = ["/log/{id}"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun getLog(@PathVariable(value = "id") id: String): ExecuteOperation = operationService.get(id)
}