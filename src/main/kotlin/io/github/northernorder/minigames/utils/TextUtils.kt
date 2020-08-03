package io.github.northernorder.minigames.utils

object TextUtils {
    fun toTitleCase(string: String): String {
        val first = string[0].toUpperCase()
        val rest = string.slice(1 until string.length)
        return "$first$rest"
    }
}