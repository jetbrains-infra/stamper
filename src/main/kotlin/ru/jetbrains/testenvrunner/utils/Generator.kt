package ru.jetbrains.testenvrunner.utils

import java.math.BigInteger
import java.security.SecureRandom

private val random = SecureRandom()

fun generateRandomWord(): String {
    return BigInteger(130, random).toString(32)
}