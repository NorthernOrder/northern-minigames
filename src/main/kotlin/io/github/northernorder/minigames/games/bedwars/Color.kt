package io.github.northernorder.minigames.games.bedwars

import net.md_5.bungee.api.ChatColor
import org.bukkit.Material
import org.bukkit.Color as ArmorColor

enum class Color(var color: ArmorColor, var bed: Material, var wool: Material, var chatColor: ChatColor) {
    BLUE(ArmorColor.BLUE, Material.BLUE_BED, Material.BLUE_WOOL, ChatColor.BLUE),
    RED(ArmorColor.RED, Material.RED_BED, Material.RED_WOOL, ChatColor.RED),
    GREEN(ArmorColor.GREEN, Material.GREEN_BED, Material.GREEN_WOOL, ChatColor.DARK_GREEN),
    YELLOW(ArmorColor.YELLOW, Material.YELLOW_BED, Material.YELLOW_WOOL, ChatColor.YELLOW),
    CYAN(ArmorColor.AQUA, Material.CYAN_BED, Material.CYAN_WOOL, ChatColor.AQUA),
    MAGENTA(ArmorColor.FUCHSIA, Material.MAGENTA_BED, Material.MAGENTA_WOOL, ChatColor.LIGHT_PURPLE),
    LIME(ArmorColor.LIME, Material.LIME_BED, Material.LIME_WOOL, ChatColor.GREEN),
    ORANGE(ArmorColor.ORANGE, Material.ORANGE_BED, Material.ORANGE_WOOL, ChatColor.GOLD);

    companion object {
        val nameList = listOf("blue", "red", "green", "yellow", "cyan", "magenta", "lime", "orange")

        fun fromName(name: String): Color? {
            return when (name) {
                "blue" -> BLUE
                "red" -> RED
                "green" -> GREEN
                "yellow" -> YELLOW
                "cyan" -> CYAN
                "magenta" -> MAGENTA
                "lime" -> LIME
                "orange" -> ORANGE
                else -> null
            }
        }
    }
}