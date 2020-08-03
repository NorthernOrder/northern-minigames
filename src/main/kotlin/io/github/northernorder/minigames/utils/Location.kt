package io.github.northernorder.minigames.utils

import org.bukkit.Bukkit

data class Location(val x: Double, val y: Double, val z: Double) {
    fun toBukkit(): org.bukkit.Location {
        return org.bukkit.Location(Bukkit.getWorld("world"), x, y, z)
    }
}