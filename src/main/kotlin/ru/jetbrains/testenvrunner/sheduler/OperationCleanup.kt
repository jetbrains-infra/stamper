package ru.jetbrains.testenvrunner.sheduler

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import ru.jetbrains.testenvrunner.service.OperationService
import ru.jetbrains.testenvrunner.service.StackService
import ru.jetbrains.testenvrunner.utils.DateUtils

@Component
class OperationCleanup constructor(val operationService: OperationService,
                                   val stackService: StackService,
                                   val dateUtils: DateUtils) {

    @Scheduled(cron = "0/10 * * * * *")
    fun cleanupOperation() {
        val stacks = stackService.getAllStacks()
        val stacksOperations = stacks.map { it.operations }.flatten().map { operationService.get(it) }.toSet()
        val allOperations = operationService.getAll().toSet()
        val unusedOperations = allOperations.minus(stacksOperations).toList()
        val oldOperations = unusedOperations.filter {
            dateUtils.addDaysToDate(it.creatingDate, 1).before(dateUtils.getCurrentDate())
        }
        operationService.removeAll(oldOperations)
    }
}