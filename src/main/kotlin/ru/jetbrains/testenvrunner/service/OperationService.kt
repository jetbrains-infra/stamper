package ru.jetbrains.testenvrunner.service

import org.springframework.stereotype.Service
import ru.jetbrains.testenvrunner.exception.NotFoundOperationException
import ru.jetbrains.testenvrunner.model.ExecuteOperation
import ru.jetbrains.testenvrunner.model.ExecuteResult
import ru.jetbrains.testenvrunner.model.OperationStatus
import ru.jetbrains.testenvrunner.repository.OperationRepository
import ru.jetbrains.testenvrunner.utils.DateUtils
import ru.jetbrains.testenvrunner.utils.generateRandomWord
import java.util.concurrent.ConcurrentHashMap

@Service
final class OperationService(private val operationRepository: OperationRepository,
                             private val dateUtils: DateUtils) {
    companion object {
        lateinit var operationService: OperationService
    }

    init {
        operationService = this
    }

    private val operations: ConcurrentHashMap<String, ExecuteOperation> = ConcurrentHashMap()

    fun create(command: String, directory: String = "", keepInSystem: Boolean = true,
               title: String = command): ExecuteOperation {
        val id = generateRandomWord()
        val executeOperation = ExecuteOperation(command, directory, ExecuteResult(), OperationStatus.CREATED, id,
                keepInSystem, title, dateUtils.getCurrentDate())
        operations[id] = executeOperation
        return executeOperation
    }

    fun removeAll(operations: List<ExecuteOperation>) = operationRepository.delete(operations)

    fun get(operationId: String): ExecuteOperation {
        var operation = operations[operationId]
        if (operation == null)
            operation = operationRepository.findById(operationId)
        return operation ?: throw NotFoundOperationException(operationId)
    }

    fun getList(operationIds: List<String>): List<ExecuteOperation> {
        return operationIds.map { get(it) }
    }

    fun fail(operation: ExecuteOperation) {
        operation.status = OperationStatus.FAILED
        complete(operation)
        println("Operation fails during performing $operation")
    }

    fun success(operation: ExecuteOperation) {
        operation.status = OperationStatus.SUCCESS
        complete(operation)
        println("Operation successfully performed $operation")
    }

    fun isCompleted(operationId: String): Boolean {
        return !operations.containsKey(operationId)
    }

    fun getAll(): List<ExecuteOperation> {
        return operationRepository.findAll()
    }

    private fun complete(operation: ExecuteOperation) {
        if (operation.keepInSystem)
            operationRepository.save(operation)
        removeFromMemory(operation.id)
    }

    private fun removeFromMemory(operationId: String) {
        operations.remove(operationId)
    }
}

