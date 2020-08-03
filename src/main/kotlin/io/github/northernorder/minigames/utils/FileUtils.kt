package io.github.northernorder.minigames.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.github.northernorder.minigames.Minigames
import java.io.File
import java.io.IOException
import java.nio.file.Paths

object FileUtils {
    private var pluginFolder: File? = null
    private val mapper: ObjectMapper = ObjectMapper().registerModule(KotlinModule()).registerModule(JavaTimeModule())

    fun setupPluginFolder() {
        pluginFolder = Minigames.instance.dataFolder
        if (!pluginFolder!!.exists()) pluginFolder!!.mkdir()
        val gamesFolder = Paths.get(pluginFolder!!.absolutePath, "games").toFile()
        if (!gamesFolder.exists()) gamesFolder.mkdir()
    }

    fun <T : Any> loadFile(file: File, dataClass: Class<T>): T? {
        var data: T? = null

        try {
            data = mapper.readValue(file, dataClass)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return data
    }

    fun <T> saveFile(data: T, file: File): Boolean {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, data)
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }

        return true
    }

    fun getPluginFolderRelativePath(vararg all: String): File {
        return Paths.get(pluginFolder!!.absolutePath, *all).toFile()
    }
}