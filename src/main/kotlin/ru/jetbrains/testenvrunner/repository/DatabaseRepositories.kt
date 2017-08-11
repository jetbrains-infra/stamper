package ru.jetbrains.testenvrunner.repository

import org.springframework.data.mongodb.repository.MongoRepository
import ru.jetbrains.testenvrunner.model.ExecuteOperation
import ru.jetbrains.testenvrunner.model.Stack
import ru.jetbrains.testenvrunner.model.User

interface UserRepository : MongoRepository<User, String> {
    fun findByEmail(firstName: String): User?

}

interface StackRepository : MongoRepository<Stack, String> {
    fun findByName(firstName: String): Stack?
}

interface OperationRepository : MongoRepository<ExecuteOperation, String> {
    fun findById(id: String): ExecuteOperation?
}
