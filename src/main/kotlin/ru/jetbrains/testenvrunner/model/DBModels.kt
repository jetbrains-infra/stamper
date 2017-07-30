package ru.jetbrains.testenvrunner.model

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.PersistenceConstructor

data class User @PersistenceConstructor constructor(val name: String, @Id val email: String, var listOfStacks: MutableList<String>)

data class Stack @PersistenceConstructor constructor(val name: String, val createdDate: String, val user: User)
