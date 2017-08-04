package ru.jetbrains.testenvrunner.repository

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository

@Repository
class TemplateRepository constructor(@Value("\${templates}") templateFolder: String) : ScriptRepository(
        templateFolder)

