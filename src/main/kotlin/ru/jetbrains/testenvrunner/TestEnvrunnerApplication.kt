package ru.jetbrains.testenvrunner

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class TestEnvrunnerApplication

fun main(args: Array<String>) {
    SpringApplication.run(TestEnvrunnerApplication::class.java, *args)
}
