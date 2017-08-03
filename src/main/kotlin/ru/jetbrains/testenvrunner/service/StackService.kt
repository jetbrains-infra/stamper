package ru.jetbrains.testenvrunner.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ru.jetbrains.testenvrunner.model.Stack
import ru.jetbrains.testenvrunner.model.StackExecutor
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
        val dateUtils: DateUtils,
        @Value("\${expire_day}") expireDateString: String,
        @Value("\${notify_day}") notifyDateString: String) {

    val expireDate = expireDateString.toInt()
    val notifyDate = notifyDateString.toInt()

    /**
     * @return all run Stack in the system
     */
    fun getAllStacks(): List<Stack> {
        return stackRepository.findAll()
    }

    /**
     * get status of stack
     * @param stack that is checked
     * @return string with status of this stack
     */
    fun getStatus(stack: Stack): String {
        val script = stackFilesRepository.get(stack.name)
        return terraformExecutorService.getStatus(script).output
    }

    /**
     * Get stack by name,
     * if stack does not exist in the system return null
     */
    fun getStack(name: String): Stack? {
        return stackRepository.findByName(name)
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

    /**
     * Async run stack from template with params by user
     * @param templateName name of running template
     * @param stackName name of running stack
     * @param parameterMap running params of stack
     * @param user user that run script
     * @return result handler
     */
    fun runStack(templateName: String, stackName: String, parameterMap: Map<String, String>,
                 user: User?): StackExecutor {
        val templateScript = templateRepository.get(templateName)

        if (user == null) {
            throw Exception("Sorry, you should bw logged before to run stack")
        }
        val stackDir = stackFilesRepository.create(stackName, templateScript, parameterMap)
        val result = terraformExecutorService.applyTerraformScript(stackDir)
        val stack = createStack(stackName, user)

        return StackExecutor(stack, result)
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
                dateUtils.addDaysToDate(currentDate, expireDate))
    }

    /**
     * Handle a success run command
     * @param stackExecutor executor of stack command
     * @return result of script execution
     */
    fun runStackSuccess(stackExecutor: StackExecutor) {
        val stack = stackExecutor.stack
        val user = stack.user
        user.listOfStacks.add(stack.name)
        userRepository.save(user)
        stackRepository.save(stack)
    }

    /**
     * Handle a error run command
     * @param stackExecutor executor of stack command
     * @return result of script execution
     */
    fun runStackError(stackExecutor: StackExecutor) {
        val stack = stackExecutor.stack
        stackFilesRepository.remove(stack.name)
    }

    /**
     * Async destroy running stack
     * @param stackName name of stack that should be destroyed
     * @return result of execution
     */
    fun destroyStack(stackName: String): StackExecutor {
        val script = stackFilesRepository.get(stackName)
        val stack = stackRepository.findByName(stackName) ?: throw Exception("The stack is not found in the Database")
        val result = terraformExecutorService.destroyTerraformScript(script)
        return StackExecutor(stack, result)
    }

    /**
     * Sync destroy running stack
     * @param stackName name of stack that should be destroyed
     */
    fun destroyStackSync(stackName: String) {
        val stackExecutor = destroyStack(stackName)
        stackExecutor.executeResultHandler.waitFor()
        if (stackExecutor.executeResultHandler.exception != null) {
            throw stackExecutor.executeResultHandler.exception!!
        }
        destroySuccess(stackExecutor)
    }

    /**
     * Handle a success run command
     * @param stackExecutor executor of stack command
     * @return result of script execution
     */
    fun destroySuccess(stackExecutor: StackExecutor) {
        val stack = stackExecutor.stack
        val user = userRepository.findByEmail(stack.user.email) ?: throw Exception("The user is not found in Database")

        stackFilesRepository.remove(stack.name)
        user.listOfStacks.remove(stack.name)
        userRepository.save(user)
        stackRepository.removeByName(stack.name)
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
}