package io.github.northernorder.minigames.games.bedwars

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

interface ShopStatic {
    fun done(item: ItemStack)
    fun setCost(cost: Cost, material: Material)
}