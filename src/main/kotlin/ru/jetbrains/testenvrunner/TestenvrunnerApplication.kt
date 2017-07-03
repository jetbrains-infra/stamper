package ru.jetbrains.testenvrunner

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class TestenvrunnerApplication

fun main(args: Array<String>) {
    SpringApplication.run(TestenvrunnerApplication::class.java, *args)
}
