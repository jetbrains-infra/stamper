package ru.jetbrains.testenvrunner.model

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.PersistenceConstructor
import java.util.*

data class User @PersistenceConstructor constructor(val name: String, @Id val email: String,
                                                    var listOfStacks: MutableList<String>)

data class Stack @PersistenceConstructor constructor(@Id val name: String,
                                                     val user: User,
                                                     val createdDate: Date,
                                                     var notificationDate: Date,
                                                     var expiredDate: Date)


