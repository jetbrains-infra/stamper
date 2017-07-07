package ru.jetbrains.testenvrunner

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class TestEnvRunnerApplication

fun main(args: Array<String>) {
    SpringApplication.run(TestEnvRunnerApplication::class.java, *args)
}
