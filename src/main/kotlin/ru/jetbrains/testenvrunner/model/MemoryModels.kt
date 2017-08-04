package ru.jetbrains.testenvrunner.model

import ru.jetbrains.testenvrunner.utils.ExecuteResultHandler
import java.io.File

fun createTerraformParam(name: String, param: Map<String, Any?>): TerraformScriptParam {
    return TerraformScriptParam(name = name,
            value = param["value"] as String? ?: "",
            defaultValue = param["default"] as String? ?: "",
            description = param["description"] as String? ?: ""
    )
}

data class TerraformScriptParam(val name: String,
                                val value: String = "",
                                val defaultValue: String = "",
                                val description: String = "") {

    val dockerHub: String get() {
        val SIGN_OF_DOCKER_HUB = "tags:"
        val lowerDescription = description.toLowerCase()
        if (!lowerDescription.contains(SIGN_OF_DOCKER_HUB)) return ""
        return lowerDescription.substringAfter(SIGN_OF_DOCKER_HUB)
    }

    var availableValues: List<String> = emptyList()
}

class TerraformScriptParams : ArrayList<TerraformScriptParam>()

data class TerraformScript(val scriptDir: File, val params: TerraformScriptParams) {
    val absolutePath: String
        get() = scriptDir.canonicalPath
    val name: String
        get() = scriptDir.name
}

data class StackExecutor(val stack: Stack, val executeResultHandler: ExecuteResultHandler) {
    val id = executeResultHandler.id
}

