package ru.jetbrains.testenvrunner.model

import ru.jetbrains.testenvrunner.utils.ExecuteResultHandler
import java.io.File

data class TerraformScript(val scriptDir: File, val params: Map<String, Any?> = emptyMap()) {
    val absolutePath: String
        get() = scriptDir.absolutePath
    val name: String
        get() = scriptDir.name
}

data class StackExecutor(val stack: Stack, val executeResultHandler: ExecuteResultHandler) {
    val id = executeResultHandler.id
}

