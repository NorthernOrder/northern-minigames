package io.github.northernorder.minigames.utils

import kotlin.math.floor

object LocationUtils {
    fun fromBukkit(location: org.bukkit.Location): Location {
        return Location(floor(location.x) + 0.5, floor(location.y) +0.5, floor(location.z) + 0.5)
    }
}