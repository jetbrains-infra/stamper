package ru.jetbrains.testenvrunner.model

import java.io.File

data class ExecutionCommand(var command: String = "")

data class ExecutionResult(val output: String = "", val exitValue: Int = -1)

data class TerraformScript(val scriptDir: File, val params: Map<String, Any?> = emptyMap()) {
    val absolutePath: String
        get() = scriptDir.absolutePath
    val name: String
        get() = scriptDir.name
}