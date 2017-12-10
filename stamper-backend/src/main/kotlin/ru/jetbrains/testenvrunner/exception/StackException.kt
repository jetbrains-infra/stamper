package ru.jetbrains.testenvrunner.exception

import ru.jetbrains.testenvrunner.model.Stack

/**
 * Abstract class of all stack exceptions
 */
abstract class StackException : Exception()

/**
 * Thrown when tries to delete the stack when it is not destroyed
 */
class DeleteBeforeDestroyException(val stack: Stack) : StackException() {
    override val message: String?
        get() = "Destroy stack ${stack.name} before deleting"
}

class StackNotFoundException : StackException()

class CreateStackWithExistNameException(private val stackName: String) : StackException() {
    override val message: String
        get() = "A stack  \"$stackName\" exists in the system"
}