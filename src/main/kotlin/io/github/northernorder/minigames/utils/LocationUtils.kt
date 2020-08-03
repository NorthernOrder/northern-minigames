package io.github.northernorder.minigames.utils

object LocationUtils {
    fun fromBukkit(location: org.bukkit.Location): Location {
        return Location(location.x, location.y, location.z)
    }
}