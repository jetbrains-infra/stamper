package ru.jetbrains.testenvrunner.model

import java.io.File

fun createTerraformParam(name: String, param: Map<String, Any?>): TerraformScriptParam {
    return TerraformScriptParam(name = name,
            value = param["default"] as String? ?: "",
            description = param["description"] as String? ?: ""
    )
}

data class TerraformScriptParam(val name: String,
                                var value: String = "",
                                val description: String = "") {

    var availableValues: List<String> = emptyList()
    var msg: String = ""
}

class TerraformScriptParams : ArrayList<TerraformScriptParam>()

data class TerraformScript(val scriptDir: File, val params: TerraformScriptParams) {
    val absolutePath: String
        get() = scriptDir.canonicalPath
    val name: String = scriptDir.name
}

