package ru.jetbrains.testenvrunner.model

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.PersistenceConstructor
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.CompoundIndexes
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken
import java.util.*

@Document
@CompoundIndexes(CompoundIndex(name = "i_username", def = "{'username': 1}"), CompoundIndex(name = "i_series", def = "{'series': 1}"))
class Token @PersistenceConstructor constructor(@Id val id: String?,
                                                username: String,
                                                series: String,
                                                tokenValue: String,
                                                date: Date) : PersistentRememberMeToken(username, series, tokenValue, date)