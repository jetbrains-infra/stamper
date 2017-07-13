package ru.jetbrains.testenvrunner.repository

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import ru.jetbrains.testenvrunner.model.TerraformScript
import java.io.File
import java.io.FileFilter
import java.io.IOException

@Repository
class ScriptRepository constructor(@Value("\${datadir}") val scriptFolder: String) {
    //consts
    val MSG_DIR_DOES_NOT_EXIST = ("The script %s does not exist in the system")

    fun getAll(): List<TerraformScript> {
        val directories = File(scriptFolder).listFiles(FileFilter { it.isDirectory })
        val scripts = directories.map { TerraformScript(it) }
        return scripts.toList()
    }

    fun get(name: String): TerraformScript {
        val script = File("$scriptFolder$name")
        if (!script.exists())
            throw IOException(MSG_DIR_DOES_NOT_EXIST.format(name))
        return TerraformScript(script)
    }
}
