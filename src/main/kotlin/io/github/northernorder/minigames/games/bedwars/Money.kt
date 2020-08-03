package io.github.northernorder.minigames.games.bedwars

import org.bukkit.Material

enum class Money {
    IRON,
    GOLD,
    EMERALD,
    DIAMOND;

    companion object {
        val items = listOf(Material.IRON_INGOT, Material.GOLD_INGOT, Material.DIAMOND, Material.EMERALD)

        fun fromName(name: String): Money? {
            return when (name) {
                "iron" -> IRON
                "gold" -> GOLD
                "emerald" -> EMERALD
                "diamond" -> DIAMOND
                else -> null
            }
        }
    }
}