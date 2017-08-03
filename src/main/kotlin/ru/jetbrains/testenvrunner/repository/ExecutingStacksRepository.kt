package ru.jetbrains.testenvrunner.repository

import org.springframework.stereotype.Component
import ru.jetbrains.testenvrunner.model.StackExecutor

/**
 * This repository keep in memory running stackExecutors
 */
@Component
class ExecutingStacksRepository {
    val NSG_NOT_FOUND_ERROR = "The '%s' StackExecutor is not found!"

    val map: MutableMap<String, StackExecutor> = mutableMapOf()

    fun add(stackName: String, stackExecutor: StackExecutor) {
        map[stackName] = stackExecutor
    }

    fun get(name: String) = map[name] ?: throw Exception(NSG_NOT_FOUND_ERROR.format(name))
    fun remove(name: String) = map.remove(name) ?: throw Exception(NSG_NOT_FOUND_ERROR.format(name))
}