package ru.jetbrains.testenvrunner.service

import org.springframework.stereotype.Service
import ru.jetbrains.testenvrunner.model.ExecutionResult
import ru.jetbrains.testenvrunner.model.Stack
import ru.jetbrains.testenvrunner.model.User
import ru.jetbrains.testenvrunner.repository.StackFilesRepository
import ru.jetbrains.testenvrunner.repository.StackRepository
import ru.jetbrains.testenvrunner.repository.TemplateRepository
import ru.jetbrains.testenvrunner.repository.UserRepository
import ru.jetbrains.testenvrunner.utils.DateUtils
import ru.jetbrains.testenvrunner.utils.TerraformExecutor
import ru.jetbrains.testenvrunner.utils.TerraformExecutorException

/**
 * Methods for work with stacks
 */
@Service
class StackService constructor(val userService: UserService,
                               val templateRepository: TemplateRepository,
                               val userRepository: UserRepository,
                               val stackRepository: StackRepository,
                               val stackFilesRepository: StackFilesRepository,
                               val terraformExecutor: TerraformExecutor,
                               val dateUtils: DateUtils) {

    /**
     * Run stack from template with params by user
     * @param templateName name of running template
     * @param stackName name of running stack
     * @param parameterMap running params of stack
     * @param user user that run script
     * @return result of script execution
     */
    fun runStack(templateName: String, stackName: String, parameterMap: Map<String, String>, user: User?): ExecutionResult {
        val templateScript = templateRepository.get(templateName)

        if (user == null) {
            return ExecutionResult("Sorry, you should bw logged before to run stack")
        }
        val result = try {
            val stack = stackFilesRepository.create(stackName, templateScript, parameterMap)
            val result = terraformExecutor.applyTerraformScript(stack)
            user.listOfStacks.add(stackName)
            userRepository.save(user)
            stackRepository.save(Stack(stackName, dateUtils.getCurrentDate(), user))
            //out
            result
        } catch (e: TerraformExecutorException) {
            stackFilesRepository.remove(stackName)
            e.executionResult
        }
        return result
    }

    /**
     * Destroy running stack
     * @param stackName name of stack that should be destroyed
     * @return result of execution
     */
    fun destroyStack(stackName: String): ExecutionResult {
        val stack = stackFilesRepository.get(stackName)

        val result = try {
            val result = terraformExecutor.destroyTerraformScript(stack)
            stackFilesRepository.remove(stackName)

            val stackInRepo = stackRepository.findByName(stackName) ?: throw Exception()
            val user = userRepository.findByEmail(stackInRepo.user.email) ?: throw Exception()
            user.listOfStacks.remove(stackName)
            userRepository.save(user)
            stackRepository.removeByName(stackInRepo.name)
            //out
            result
        } catch (e: TerraformExecutorException) {
            e.executionResult
        }
        return result
    }
}