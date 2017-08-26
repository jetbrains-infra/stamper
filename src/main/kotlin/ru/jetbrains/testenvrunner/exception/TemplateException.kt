package ru.jetbrains.testenvrunner.exception

import ru.jetbrains.testenvrunner.model.TerraformScript

abstract class TemplateException : Exception()

class TemplateWithoutNameException(val template: TerraformScript)
