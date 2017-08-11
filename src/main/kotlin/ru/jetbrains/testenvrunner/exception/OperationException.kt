package ru.jetbrains.testenvrunner.exception

/**
 * Exception is thrown when the operation not found in operation service
 */
class NotFoundOperationException(val operationId: String) : Exception() {
    override val message: String?
        get() = "Operation $operationId not found.$stackTrace"
}