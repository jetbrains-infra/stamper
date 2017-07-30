package ru.jetbrains.testenvrunner.service

import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository
import org.springframework.stereotype.Component
import ru.jetbrains.testenvrunner.model.Token
import ru.jetbrains.testenvrunner.repository.TokenRepository
import java.util.*
import javax.inject.Inject

@Component
class TokenService : PersistentTokenRepository {
    @Inject
    lateinit var repository: TokenRepository

    override fun createNewToken(token: PersistentRememberMeToken) {
        repository.save<Token>(Token(null,
                token.username,
                token.series,
                token.tokenValue,
                token.date))
    }

    override fun updateToken(series: String, value: String, lastUsed: Date) {
        val token = repository.findBySeries(series) ?: throw Exception("Token not found. Series: $series")
        repository.save<Token>(Token(token.id, token.username, series, value, lastUsed))
    }

    override fun getTokenForSeries(seriesId: String): Token? {
        return repository.findBySeries(seriesId)
    }

    override fun removeUserTokens(username: String) {
        val token = repository.findByUsername(username)
        if (token != null) {
            repository.delete(token)
        }
    }
}