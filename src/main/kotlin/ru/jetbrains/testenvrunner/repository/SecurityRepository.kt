package ru.jetbrains.testenvrunner.repository

import org.springframework.data.mongodb.repository.MongoRepository
import ru.jetbrains.testenvrunner.model.Token

interface TokenRepository : MongoRepository<Token, String> {
    fun findBySeries(series: String): Token?
    fun findByUsername(username: String): Token?
}