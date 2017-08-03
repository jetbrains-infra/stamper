package ru.jetbrains.testenvrunner.controller

import org.springframework.web.bind.annotation.*
import ru.jetbrains.testenvrunner.model.Stack
import ru.jetbrains.testenvrunner.repository.ExecutingStacksRepository
import ru.jetbrains.testenvrunner.service.StackService
import ru.jetbrains.testenvrunner.utils.ExecuteResultParticle

@RestController
@RequestMapping("/api/")
class RestWebController constructor(
        val executorStackRepository: ExecutingStacksRepository,
        val stackService: StackService) {

    @RequestMapping(value = "/new-output", method = arrayOf(RequestMethod.GET))
    fun receiveNewOuptput(@RequestParam("id") id: String): ExecuteResultParticle {
        val result = executorStackRepository.get(id)
        return result.executeResultHandler.executeResultParticle
    }

    @RequestMapping(value = "/end-new-output", method = arrayOf(RequestMethod.DELETE))
    @ResponseBody
    fun stopReceiveData(@RequestBody data: Map<String, String>): Map<String, Boolean> {
        val name = data["id"] ?: throw Exception("oops!")
        val command = data["command"] ?: throw Exception("oops!")
        val stackExecutor = executorStackRepository.get(name)

        val exception = stackExecutor.executeResultHandler.exception

        var success = true
        if (exception == null) {
            when (command) {
                "apply" -> stackService.runStackSuccess(stackExecutor)
                "destroy" -> stackService.destroySuccess(stackExecutor)
            }
        } else {
            success = false
            when (command) {
                "apply" -> stackService.runStackError(stackExecutor)
            }
        }

        executorStackRepository.remove(name)
        return mapOf("success" to success)
    }

    @RequestMapping(value = "/stack/{id}", method = arrayOf(RequestMethod.GET))
    @ResponseBody
    fun getStackById(@PathVariable(value = "id") stackName: String): Stack? {
        return stackService.getStack(stackName)
    }

}