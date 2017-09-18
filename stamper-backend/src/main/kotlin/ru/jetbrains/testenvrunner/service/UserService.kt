package ru.jetbrains.testenvrunner.service

import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.stereotype.Service
import ru.jetbrains.testenvrunner.model.User
import ru.jetbrains.testenvrunner.repository.UserRepository

@Service
class UserService constructor(val userRepository: UserRepository) {
    /**
     * Get user by authentication data
     */
    fun getUserByAuth(auth: OAuth2Authentication?): User? {
        val details = auth?.userAuthentication?.details as Map<*, *>? ?: return null
        val email = details["email"] as String? ?: "None"
        val name = details["name"] as String? ?: "None"
        return userRepository.findByEmail(email) ?: User(name, email, mutableListOf())
    }
}