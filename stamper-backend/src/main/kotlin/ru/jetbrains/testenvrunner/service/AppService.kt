package ru.jetbrains.testenvrunner.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class AppService(
        @Value("\${frontend_address}") val frontendAddress: String,
        @Value("\${backend_address}") val backendAddress: String)