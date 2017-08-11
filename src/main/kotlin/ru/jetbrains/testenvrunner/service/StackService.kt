package ru.jetbrains.testenvrunner.service

import org.apache.commons.exec.ExecuteException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ru.jetbrains.testenvrunner.exception.DeleteBeforeDestroyException
import ru.jetbrains.testenvrunner.model.ExecuteOperation
import ru.jetbrains.testenvrunner.model.Stack
import ru.jetbrains.testenvrunner.model.StackStatus
import ru.jetbrains.testenvrunner.model.User
import ru.jetbrains.testenvrunner.repository.StackFilesRepository
import ru.jetbrains.testenvrunner.repository.StackRepository
import ru.jetbrains.testenvrunner.repository.TemplateRepository
import ru.jetbrains.testenvrunner.repository.UserRepository
import ru.jetbrains.testenvrunner.utils.DateUtils

/**
 * Methods for work with stacks
 */
@Service
class StackService constructor(
        val templateRepository: TemplateRepository,
        val userRepository: UserRepository,
        val stackRepository: StackRepository,
        val stackFilesRepository: StackFilesRepository,
        val terraformExecutorService: TerraformExecutorService,
        val operationService: OperationService,
        val dateUtils: DateUtils,
        @Value("\${expire_day}") val expireDate: Int,
        @Value("\${notify_day}") val notifyDate: Int) {

    /**
     * @return all run Stack in the system
     */
    fun getAllStacks(): List<Stack> {
        val stacks = stackRepository.findAll()
        stacks.forEach { setStatus(it) }
        return stacks
    }

    /**
     * Get status of stack
     * @param stack that is checked
     * @return string with status of this stack
     */
    fun getStatus(stack: Stack): String {
        val script = stackFilesRepository.get(stack.name)
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
        val lastOperation = stack.operations.last()
        return !operationService.isCompleted(lastOperation)
    }

    /**
     * Get stack by name,
     * if stack does not exist in the system return null
     */
    fun getStack(name: String): Stack? {
        val stack = stackRepository.findByName(name)
        if (stack == null) return stack
        setStatus(stack)
        return stack
    }

    private fun setStatus(stack: Stack) {
        stack.status = StackStatus.APPLIED
        if (isLoading(stack)) stack.status = StackStatus.IN_PROGRESS
        if (isFailed(stack)) stack.status = StackStatus.FAILED
        if (isDestroyed(stack)) stack.status = StackStatus.DESTROYED

    }

    private fun isFailed(stack: Stack): Boolean {
        val lastOperation = stack.operations.last()
        return operationService.isFailed(lastOperation)
    }

    /**
     * Get use link for Stack
     * @param stack, that is run
     * @return using link
     */
    fun getStackRunLink(stack: Stack): String {
        val terraformScript = stackFilesRepository.get(stack.name)
        return terraformExecutorService.getRunLink(terraformScript)
    }

    fun getComplectedStackOperations(stack: Stack): List<ExecuteOperation> {
        val operations = operationService.getList(stack.operations)
        if (isLoading(stack))
            return operations.subList(0, operations.size - 1)
        return operations
    }

    /**
     * Async run stack from template with params by user
     * @param templateName name of running template
     * @param stackName name of running stack
     * @param parameterMap running params of stack
     * @param user user that run script
     * @return result handler
     */
    fun runStack(templateName: String, stackName: String, parameterMap: Map<String, String>,
                 user: User?): String {
        val templateScript = templateRepository.get(templateName)
        if (user == null) {
            throw Exception("Sorry, you should bw logged before to run stack")
        }
        val stackDir = stackFilesRepository.create(stackName, templateScript, parameterMap)
        val stack = createStack(stackName, user)
        val id = terraformExecutorService.applyTerraformScript(stackDir)
        stack.operations.add(id)
        saveStack(stack)
        return id
    }

    /**
     * Async destroy running stack
     * @param stackName name of stack that should be destroyed
     * @return operation ID
     */
    fun destroyStack(stackName: String): String {
        val script = stackFilesRepository.get(stackName)
        val id = terraformExecutorService.destroyTerraformScript(script)
        val stack = stackRepository.findByName(stackName) ?: throw Exception("The stack is not found in the Database")
        stack.operations.add(id)
        stackRepository.save(stack)
        return id
    }

    /**
     * Delete the stack
     * @param stackName name of stack that should be deleted
     * @throws [DeleteBeforeDestroyException] if the user tries to delete the not destroyed stack
     */
    fun deleteStack(stackName: String) {
        val stack = stackRepository.findByName(stackName) ?: throw Exception("The stack is not found in Database")
        val user = userRepository.findByEmail(stack.user.email) ?: throw Exception("The user is not found in Database")
        if (!isDestroyed(stack)) {
            throw DeleteBeforeDestroyException(stack)
        }
        operationService.removeAll(stack.operations)
        stackFilesRepository.remove(stack.name)
        user.listOfStacks.remove(stack.name)
        userRepository.save(user)
        stackRepository.delete(stack)
    }

    private fun isDestroyed(stack: Stack): Boolean {
        val status = getStatus(stack)
        return status.isEmpty() || status.startsWith("\n\nOutputs:")
    }

    /**
     * Create stack
     * @param stackName - name of Stack
     * @param user - user that run Stack
     * @return created Stack
     */
    fun createStack(stackName: String, user: User): Stack {
        val currentDate = dateUtils.getCurrentDate()
        return Stack(stackName, user, currentDate, dateUtils.addDaysToDate(currentDate, notifyDate),
                dateUtils.addDaysToDate(currentDate, expireDate), mutableListOf(), StackStatus.IN_PROGRESS)
    }

    /**
     * Save stack in DB
     * @param stack that will be saved
     */
    private fun saveStack(stack: Stack) {
        val user = stack.user
        user.listOfStacks.add(stack.name)
        userRepository.save(user)
        stackRepository.save(stack)
    }

    /**
     * Get stacks that are expired
     */
    fun getExpiredStacks(): List<Stack> {
        return getAllStacks().filter { dateUtils.getCurrentDate().after(it.expiredDate) }
    }

    /**
     * Get stacks which users should be notified about stacks that will be expired
     */
    fun getNotifyStacks(): List<Stack> {
        return getAllStacks().filter { dateUtils.getCurrentDate().after(it.notificationDate) }
    }

    /**
     * Update notification date of stack
     */
    fun updateNotificationDates(vararg stacks: Stack) {
        stacks.forEach { it.notificationDate = dateUtils.addDaysToDate(dateUtils.getCurrentDate(), notifyDate) }
        stackRepository.save(stacks.asIterable())
    }

    /**
     * Prolong the expire date
     * @param stacks that will prolonged
     */
    fun prolongExpireDate(vararg stacks: Stack) {
        val curDate = dateUtils.getCurrentDate()
        stacks.forEach { it.expiredDate = dateUtils.addDaysToDate(curDate, expireDate) }
        stacks.forEach { it.notificationDate = dateUtils.addDaysToDate(curDate, notifyDate) }
        stackRepository.save(stacks.asIterable())
    }

    fun getRunningCommandId(stack: Stack): String? {
        val lastOperation = stack.operations.last()
        if (operationService.isCompleted(lastOperation)) {
            return null
        } else {
            return lastOperation
        }
    }
}