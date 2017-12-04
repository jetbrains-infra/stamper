package ru.jetbrains.testenvrunner.sheduler

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import ru.jetbrains.testenvrunner.service.StackInfoService
import ru.jetbrains.testenvrunner.service.StackService

@Component
class StackCleanup constructor(val stackInfoService: StackInfoService,
                               val stackService: StackService) {

    @Scheduled(cron = "0 0 * * * *")
    fun cleanupOperation() {
        stackService.getAllDestroyedStacks().forEach { stackInfoService.deleteStack(it) }
    }
}