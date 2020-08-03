package io.github.northernorder.minigames.games.bedwars

import org.bukkit.Material

enum class Upgrade {
    SHARPNESS,
    PROTECTION,
    FORGE,
    HEAL,
    TRAP;

    companion object {
        fun fromMaterial(material: Material): Upgrade? {
            return when (material) {
                Material.IRON_SWORD -> SHARPNESS
                Material.DIAMOND_CHESTPLATE -> PROTECTION
                Material.ANVIL -> FORGE
                Material.BEACON -> HEAL
                Material.FEATHER -> TRAP
                else -> null
            }
        }
    }
}