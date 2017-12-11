package ru.jetbrains.testenvrunner.exception

/**
 * Abstract class of all stack exceptions
 */
abstract class StackException : Exception()

class StackNotFoundException : StackException()

class CreateStackWithExistNameException(private val stackName: String) : StackException() {
    override val message: String
        get() = "A stack  \"$stackName\" exists in the system"
}