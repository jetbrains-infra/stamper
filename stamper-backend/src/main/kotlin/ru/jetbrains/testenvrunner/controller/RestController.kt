package ru.jetbrains.testenvrunner.controller

import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.web.bind.annotation.*
import ru.jetbrains.testenvrunner.exception.StackNotFoundException
import ru.jetbrains.testenvrunner.model.*
import ru.jetbrains.testenvrunner.repository.TemplateRepository
import ru.jetbrains.testenvrunner.service.*


@RestController
@RequestMapping("/api/")
class RestWebController constructor(
        val stackService: StackService,
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
    fun destroyStack(@PathVariable(value = "id") stackName: String, @RequestParam(
            value = "force") force: Boolean): String {
        if (force) {
            stackInfoService.deleteStack(stackName)
            return "OK"
        }
        return stackService.destroyStack(stackName)
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
        return stackService.getAllStacks()
    }

    @RequestMapping("/user")
    fun getAuthUser(auth: OAuth2Authentication?): User? {
        return userService.getUserByAuth(auth)
    }
}

data class OutputStatus(val stackStatus: StackStatus, val output: String? = null,
                        val commandId: String?)