package ru.jetbrains.testenvrunner.service

import mu.KotlinLogging
import org.springframework.stereotype.Component
import ru.jetbrains.testenvrunner.exception.StackNotFoundException
import ru.jetbrains.testenvrunner.model.ExecuteOperation
import ru.jetbrains.testenvrunner.model.Stack
import ru.jetbrains.testenvrunner.model.StackStatus
import ru.jetbrains.testenvrunner.repository.StackRepository

@Component
class TerraformResultHandler(val stackRepository: StackRepository,
                             val stackInfoService: StackInfoService) : OperationResutHandler {
    private val logger = KotlinLogging.logger {}

    fun getStackByOperationId(operationId: String): Stack = stackRepository.findByOperationsContains(
            operationId) ?: throw StackNotFoundException()

    override fun onSuccess(operation: ExecuteOperation) {
        val stack = getStackByOperationId(operation.id)
        when (operation.title) {
            "terraform apply" -> {
                stack.status = StackStatus.APPLIED
                stack.params = stackInfoService.getParams(stack)
                stackRepository.save(stack)
                logger.debug { "Operation 'terraform apply' with id ${operation.id} is successful completed." }
            }
            "terraform destroy" -> {
                stackInfoService.markStackDeleted(stack.name)
                logger.debug { "Operation 'terraform destroy' with id ${operation.id} is successful completed." }
            }
        }
    }

    override fun onFail(operation: ExecuteOperation) {
        val stack = getStackByOperationId(operation.id)
        stack.status = StackStatus.FAILED
        stackRepository.save(stack)
        logger.error { "Operation ${operation.command} with id ${operation.id} is failed.\n Exception: ${operation.executeResult.exception}" }
    }
}