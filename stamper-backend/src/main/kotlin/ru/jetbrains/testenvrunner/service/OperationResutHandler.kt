package ru.jetbrains.testenvrunner.service

import ru.jetbrains.testenvrunner.model.ExecuteOperation

interface OperationResutHandler {
    fun onSuccess(operation: ExecuteOperation)

    fun onFail(operation: ExecuteOperation)
}