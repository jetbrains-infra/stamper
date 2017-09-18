package ru.jetbrains.testenvrunner.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ru.jetbrains.testenvrunner.exception.StackNotFoundException
import ru.jetbrains.testenvrunner.model.Stack
import ru.jetbrains.testenvrunner.model.StackStatus
import ru.jetbrains.testenvrunner.model.TerraformScript
import ru.jetbrains.testenvrunner.model.User
import ru.jetbrains.testenvrunner.repository.StackDirectoryRepository
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
        val stackDirectoryRepository: StackDirectoryRepository,
        val terraformExecutorService: TerraformExecutorService,
        val dateUtils: DateUtils,
        val terraformResultHandler: TerraformResultHandler,
        @Value("\${expire_day}") val expireDate: Int,
        @Value("\${notify_day}") val notifyDate: Int) {

    /**
     * @return all run Stack in the system
     */
    fun getAllStacks(): List<Stack> = stackRepository.findAll()

    /**
     * Get stack by name,
     * if stack does not exist in the system return null
     */
    fun getStack(name: String): Stack? = stackRepository.findByName(name)

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
        val stackDir = stackDirectoryRepository.create(stackName, templateScript, parameterMap)
        val stack = createStack(stackName, user)
        return applyStack(stackDir, stack)
    }

    fun reapplyStack(stackName: String) {
        val stackDir = stackDirectoryRepository.get(stackName)
        val stack = getStack(stackName) ?: throw StackNotFoundException()

        stack.status = StackStatus.IN_PROGRESS
        stackRepository.save(stack)

        val id = terraformExecutorService.applyTerraformScript(stackDir, terraformResultHandler)
        stack.operations.add(id)
        stackRepository.save(stack)
    }

    /**
     * Async destroy running stack
     * @param stackName name of stack that should be destroyed
     * @return operation ID
     */
    fun destroyStack(stackName: String): String {
        val stack = stackRepository.findByName(stackName) ?: throw Exception("The stack is not found in the Database")
        val script = stackDirectoryRepository.get(stackName)

        stack.status = StackStatus.IN_PROGRESS
        stackRepository.save(stack)

        val id = terraformExecutorService.destroyTerraformScript(script, terraformResultHandler)
        stack.operations.add(id)
        stackRepository.save(stack)

        return id
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

    /**
     * Create stack
     * @param stackName - name of Stack
     * @param user - user that run Stack
     * @return created Stack
     */
    fun createStack(stackName: String, user: User): Stack {
        val currentDate = dateUtils.getCurrentDate()
        return Stack(name = stackName, user = user, createdDate = currentDate,
                notificationDate = dateUtils.addDaysToDate(currentDate, notifyDate),
                expiredDate = dateUtils.addDaysToDate(currentDate, expireDate), operations = mutableListOf(),
                status = StackStatus.IN_PROGRESS, params = emptyMap())
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

    private fun applyStack(stackDir: TerraformScript,
                           stack: Stack): String {
        val id = terraformExecutorService.applyTerraformScript(stackDir, terraformResultHandler)
        stack.operations.add(id)
        stack.status = StackStatus.IN_PROGRESS
        saveStack(stack)
        return id
    }
}