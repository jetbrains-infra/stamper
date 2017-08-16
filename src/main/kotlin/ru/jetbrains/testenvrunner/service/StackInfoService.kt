package ru.jetbrains.testenvrunner.service

import org.apache.commons.exec.ExecuteException
import org.springframework.stereotype.Service
import ru.jetbrains.testenvrunner.exception.DeleteBeforeDestroyException
import ru.jetbrains.testenvrunner.model.ExecuteOperation
import ru.jetbrains.testenvrunner.model.Stack
import ru.jetbrains.testenvrunner.model.StackStatus
import ru.jetbrains.testenvrunner.repository.StackDirectoryRepository
import ru.jetbrains.testenvrunner.repository.StackRepository
import ru.jetbrains.testenvrunner.repository.UserRepository

@Service
class StackInfoService constructor(val operationService: OperationService,
                                   val stackDirectoryRepository: StackDirectoryRepository,
                                   val terraformExecutorService: TerraformExecutorService,
                                   val stackRepository: StackRepository,
                                   val userRepository: UserRepository) {

    /**
     * Get status of stack
     * @param stack that is checked
     * @return string with status of this stack
     */
    fun getTerraformStatus(stack: Stack): String {
        val script = stackDirectoryRepository.get(stack.name)
        return try {
            terraformExecutorService.getStatus(script).output
        } catch (e: ExecuteException) {
            ""
        }
    }

    /**
     * Is the stack under an operation or not
     * @param stack that is checked
     */
    fun isLoading(stack: Stack): Boolean {
        return stack.status == StackStatus.IN_PROGRESS
    }

    /**
     * Get use link for Stack
     * @param stack, that is run
     * @return using link
     */
    fun getStackRunLink(stack: Stack): String {
        val terraformScript = stackDirectoryRepository.get(stack.name)
        return terraformExecutorService.getRunLink(terraformScript)
    }

    fun getStackLogs(stack: Stack): List<ExecuteOperation> {
        val operations = operationService.getList(stack.operations)
        if (isLoading(stack))
            return operations.subList(0, operations.size - 1)
        return operations.reversed()
    }

    fun getLastCommandId(stack: Stack): String? {
        return stack.operations.last()
    }

    /**
     * Delete the stack
     * @param stackName name of stack that should be deleted
     * @throws [DeleteBeforeDestroyException] if the user tries to delete the not destroyed stack
     */
    fun deleteStack(stackName: String) {
        val stack = stackRepository.findByName(stackName) ?: throw Exception("The stack is not found in Database")
        val user = userRepository.findByEmail(stack.user.email) ?: throw Exception("The user is not found in Database")
        stackDirectoryRepository.remove(stack.name)
        user.listOfStacks.remove(stack.name)
        userRepository.save(user)
        stackRepository.delete(stack)
    }
}