package ru.jetbrains.testenvrunner.controller

import org.springframework.web.bind.annotation.*
import ru.jetbrains.testenvrunner.exception.StackNotFoundException
import ru.jetbrains.testenvrunner.model.ExecuteOperation
import ru.jetbrains.testenvrunner.model.ExecuteResultParticle
import ru.jetbrains.testenvrunner.model.Stack
import ru.jetbrains.testenvrunner.model.StackStatus
import ru.jetbrains.testenvrunner.service.OperationService
import ru.jetbrains.testenvrunner.service.StackInfoService
import ru.jetbrains.testenvrunner.service.StackService

@RestController
@RequestMapping("/api/")
class RestWebController constructor(
        val stackService: StackService,
        val operationService: OperationService,
        val stackInfoService: StackInfoService) {

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
}

data class OutputStatus(val stackStatus: StackStatus, val output: String? = null,
                        val commandId: String?)