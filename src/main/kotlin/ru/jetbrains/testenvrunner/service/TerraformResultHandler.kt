package ru.jetbrains.testenvrunner.service

import org.springframework.stereotype.Component
import ru.jetbrains.testenvrunner.exception.StackNotFoundException
import ru.jetbrains.testenvrunner.model.ExecuteOperation
import ru.jetbrains.testenvrunner.model.Stack
import ru.jetbrains.testenvrunner.model.StackStatus
import ru.jetbrains.testenvrunner.repository.StackRepository

@Component
class TerraformResultHandler(val stackRepository: StackRepository,
                             val stackInfoService: StackInfoService) : OperationResutHandler {

    fun getStackByOperationId(operationId: String): Stack = stackRepository.findByOperationsContains(
            operationId) ?: throw StackNotFoundException()

    override fun onSuccess(operation: ExecuteOperation) {
        val stack = getStackByOperationId(operation.id)
        when (operation.title) {
            "terraform apply" -> {
                stack.status = StackStatus.APPLIED
                stack.params = stackInfoService.getParams(stack)
                stackRepository.save(stack)
            }
            "terraform destroy" -> {
                stackInfoService.deleteStack(stack.name)
            }
        }

    }

    override fun onFail(operation: ExecuteOperation) {
        val stack = getStackByOperationId(operation.id)
        stack.status = StackStatus.FAILED
        stackRepository.save(stack)
    }
}